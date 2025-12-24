const express = require('express');
const sqlite3 = require('sqlite3').verbose();
const bodyParser = require('body-parser');
const cors = require('cors');
const path = require('path');

const app = express();
const PORT = 3000;

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static('public'));

// Initialize SQLite database
const db = new sqlite3.Database('./database.db', (err) => {
    if (err) {
        console.error('Error opening database:', err.message);
    } else {
        console.log('Connected to SQLite database.');
        initializeDatabase();
    }
});

// Initialize database tables
function initializeDatabase() {
    // Create Categories table
    db.run(`CREATE TABLE IF NOT EXISTS categories (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        description TEXT,
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    )`, (err) => {
        if (err) {
            console.error('Error creating categories table:', err.message);
        } else {
            console.log('Categories table ready.');
        }
    });

    // Create Products table (linked to Categories)
    db.run(`CREATE TABLE IF NOT EXISTS products (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        description TEXT,
        price REAL NOT NULL,
        category_id INTEGER NOT NULL,
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (category_id) REFERENCES categories(id)
    )`, (err) => {
        if (err) {
            console.error('Error creating products table:', err.message);
        } else {
            console.log('Products table ready.');
            // Insert sample data if tables are empty
            insertSampleData();
        }
    });
}

// Insert sample data
function insertSampleData() {
    db.get("SELECT COUNT(*) as count FROM categories", (err, row) => {
        if (err) {
            console.error('Error checking categories:', err.message);
            return;
        }
        if (row.count === 0) {
            const categories = [
                ['Electronics', 'Electronic devices and gadgets'],
                ['Clothing', 'Apparel and fashion items'],
                ['Books', 'Books and reading materials']
            ];
            
            categories.forEach(([name, desc]) => {
                db.run("INSERT INTO categories (name, description) VALUES (?, ?)", [name, desc]);
            });

            // Insert products after a short delay to ensure categories exist
            setTimeout(() => {
                db.run("INSERT INTO products (name, description, price, category_id) VALUES (?, ?, ?, ?)",
                    ['Laptop', 'High-performance laptop', 999.99, 1]);
                db.run("INSERT INTO products (name, description, price, category_id) VALUES (?, ?, ?, ?)",
                    ['T-Shirt', 'Cotton t-shirt', 19.99, 2]);
                db.run("INSERT INTO products (name, description, price, category_id) VALUES (?, ?, ?, ?)",
                    ['Programming Book', 'Learn programming', 49.99, 3]);
            }, 500);
        }
    });
}

// ========== HTML ROUTES (Web Interface) ==========

// Home page - List all products
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

// Categories page
app.get('/categories', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'categories.html'));
});

// Products page
app.get('/products', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'products.html'));
});

// ========== API ROUTES (JSON for Android) ==========

// Get all categories
app.get('/api/categories', (req, res) => {
    db.all("SELECT * FROM categories ORDER BY created_at DESC", (err, rows) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json(rows);
    });
});

// Get category by ID
app.get('/api/categories/:id', (req, res) => {
    const id = req.params.id;
    db.get("SELECT * FROM categories WHERE id = ?", [id], (err, row) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        if (!row) {
            res.status(404).json({ error: 'Category not found' });
            return;
        }
        res.json(row);
    });
});

// Create new category
app.post('/api/categories', (req, res) => {
    const { name, description } = req.body;
    if (!name) {
        res.status(400).json({ error: 'Name is required' });
        return;
    }
    
    db.run("INSERT INTO categories (name, description) VALUES (?, ?)", 
        [name, description || null], 
        function(err) {
            if (err) {
                res.status(500).json({ error: err.message });
                return;
            }
            res.json({ 
                id: this.lastID, 
                name, 
                description,
                message: 'Category created successfully' 
            });
        }
    );
});

// Update category
app.put('/api/categories/:id', (req, res) => {
    const id = req.params.id;
    const { name, description } = req.body;
    
    db.run("UPDATE categories SET name = ?, description = ? WHERE id = ?",
        [name, description, id],
        function(err) {
            if (err) {
                res.status(500).json({ error: err.message });
                return;
            }
            if (this.changes === 0) {
                res.status(404).json({ error: 'Category not found' });
                return;
            }
            res.json({ message: 'Category updated successfully' });
        }
    );
});

// Delete category
app.delete('/api/categories/:id', (req, res) => {
    const id = req.params.id;
    db.run("DELETE FROM categories WHERE id = ?", [id], function(err) {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        if (this.changes === 0) {
            res.status(404).json({ error: 'Category not found' });
            return;
        }
        res.json({ message: 'Category deleted successfully' });
    });
});

// Get all products
app.get('/api/products', (req, res) => {
    db.all(`SELECT p.*, c.name as category_name, c.description as category_description 
            FROM products p 
            LEFT JOIN categories c ON p.category_id = c.id 
            ORDER BY p.created_at DESC`, (err, rows) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json(rows);
    });
});

// Get product by ID
app.get('/api/products/:id', (req, res) => {
    const id = req.params.id;
    db.get(`SELECT p.*, c.name as category_name, c.description as category_description 
            FROM products p 
            LEFT JOIN categories c ON p.category_id = c.id 
            WHERE p.id = ?`, [id], (err, row) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        if (!row) {
            res.status(404).json({ error: 'Product not found' });
            return;
        }
        res.json(row);
    });
});

// Create new product
app.post('/api/products', (req, res) => {
    const { name, description, price, category_id } = req.body;
    if (!name || !price || !category_id) {
        res.status(400).json({ error: 'Name, price, and category_id are required' });
        return;
    }
    
    db.run("INSERT INTO products (name, description, price, category_id) VALUES (?, ?, ?, ?)",
        [name, description || null, price, category_id],
        function(err) {
            if (err) {
                res.status(500).json({ error: err.message });
                return;
            }
            // Return the created product with category info
            db.get(`SELECT p.*, c.name as category_name, c.description as category_description 
                    FROM products p 
                    LEFT JOIN categories c ON p.category_id = c.id 
                    WHERE p.id = ?`, [this.lastID], (err, row) => {
                if (err) {
                    res.status(500).json({ error: err.message });
                    return;
                }
                res.json({ ...row, message: 'Product created successfully' });
            });
        }
    );
});

// Update product
app.put('/api/products/:id', (req, res) => {
    const id = req.params.id;
    const { name, description, price, category_id } = req.body;
    
    db.run("UPDATE products SET name = ?, description = ?, price = ?, category_id = ? WHERE id = ?",
        [name, description, price, category_id, id],
        function(err) {
            if (err) {
                res.status(500).json({ error: err.message });
                return;
            }
            if (this.changes === 0) {
                res.status(404).json({ error: 'Product not found' });
                return;
            }
            res.json({ message: 'Product updated successfully' });
        }
    );
});

// Delete product
app.delete('/api/products/:id', (req, res) => {
    const id = req.params.id;
    db.run("DELETE FROM products WHERE id = ?", [id], function(err) {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        if (this.changes === 0) {
            res.status(404).json({ error: 'Product not found' });
            return;
        }
        res.json({ message: 'Product deleted successfully' });
    });
});

// Start server
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server running on http://localhost:${PORT}`);
    console.log(`Access the web interface at http://localhost:${PORT}`);
    console.log(`API endpoints available at http://localhost:${PORT}/api/`);
});

// Graceful shutdown
process.on('SIGINT', () => {
    db.close((err) => {
        if (err) {
            console.error(err.message);
        }
        console.log('Database connection closed.');
        process.exit(0);
    });
});

