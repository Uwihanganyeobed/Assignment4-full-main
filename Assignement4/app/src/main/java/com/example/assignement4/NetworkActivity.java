package com.example.assignement4;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NetworkActivity extends AppCompatActivity implements 
        ProductFormDialog.OnProductCreatedListener,
        ProductAdapter.OnProductActionListener {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private RequestQueue requestQueue;
    private FloatingActionButton fabAddProduct;
    private SwipeRefreshLayout swipeRefreshLayout;

    // Base URL Configuration:
    // For Android Emulator: use "http://10.0.2.2:3000/api/products"
    // For Physical Device with USB Tethering: use your computer's IP address
    // Find your IP: Run "ipconfig" in Command Prompt, look for IPv4 Address
    // Common tethering IPs: 192.168.42.1, 192.168.43.1, 192.168.137.1
    // Current IP: 10.144.195.113 (Ethernet 7) or 172.31.118.74 (Wi-Fi)
    private static final String BASE_URL = "http://10.144.195.113:3000/api/products";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter();
        adapter.setOnProductActionListener(this);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark)
        );
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadProducts();
        });

        fabAddProduct = findViewById(R.id.fabAddProduct);
        fabAddProduct.setOnClickListener(v -> showProductFormDialog(null));

        requestQueue = Volley.newRequestQueue(this);

        // Load products
        loadProducts();
    }

    private void loadProducts() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                BASE_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Product> products = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Product product = new Product();
                                product.setId(jsonObject.getInt("id"));
                                product.setName(jsonObject.getString("name"));
                                product.setDescription(jsonObject.optString("description", null));
                                product.setPrice(jsonObject.getDouble("price"));
                                product.setCategory_id(jsonObject.getInt("category_id"));
                                product.setCategory_name(jsonObject.optString("category_name", null));
                                product.setCreated_at(jsonObject.optString("created_at", null));
                                products.add(product);
                            }
                            adapter.setProducts(products);
                        } catch (JSONException e) {
                            Toast.makeText(NetworkActivity.this,
                                    "Error parsing response: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } finally {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Error loading products";
                        String detailedError = "";
                        
                        if (error.networkResponse != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                JSONObject jsonObject = new JSONObject(responseBody);
                                errorMessage = jsonObject.optString("error", errorMessage);
                                detailedError = "Status: " + error.networkResponse.statusCode;
                            } catch (Exception e) {
                                errorMessage = "Network error: " + error.getMessage();
                                detailedError = "Status: " + error.networkResponse.statusCode;
                            }
                        } else {
                            // No network response - connection failed
                            String volleyError = error.getMessage();
                            if (volleyError != null) {
                                errorMessage = "Connection failed: " + volleyError;
                            } else {
                                errorMessage = "Connection failed: Cannot reach server";
                            }
                            
                            detailedError = "URL: " + BASE_URL + "\n\n";
                            detailedError += "Possible issues:\n";
                            detailedError += "1. Web server not running\n";
                            detailedError += "2. Wrong IP address\n";
                            detailedError += "3. Firewall blocking port 3000\n";
                            detailedError += "4. USB tethering not enabled\n\n";
                            detailedError += "Test in phone browser:\n";
                            detailedError += BASE_URL.replace("/api/products", "");
                        }
                        
                        // Show detailed error in a dialog
                        new android.app.AlertDialog.Builder(NetworkActivity.this)
                                .setTitle("Network Error")
                                .setMessage(errorMessage + "\n\n" + detailedError)
                                .setPositiveButton("OK", null)
                                .show();
                        
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        requestQueue.add(request);
    }

    private void showProductFormDialog(Product product) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ProductFormDialog dialog = new ProductFormDialog();
        dialog.setOnProductCreatedListener(this);
        if (product != null) {
            dialog.setEditingProduct(product);
        }
        dialog.show(fragmentManager, "ProductFormDialog");
    }

    @Override
    public void onProductCreated(Product product) {
        // Reload all products to ensure sync with server
        loadProducts();
    }

    @Override
    public void onEditClick(Product product) {
        showProductFormDialog(product);
    }

    @Override
    public void onDeleteClick(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete \"" + product.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> deleteProduct(product))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteProduct(Product product) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                BASE_URL + "/" + product.getId(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(NetworkActivity.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                        adapter.removeProduct(product);
                        loadProducts();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Error deleting product";
                        if (error.networkResponse != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                JSONObject jsonObject = new JSONObject(responseBody);
                                errorMessage = jsonObject.optString("error", errorMessage);
                            } catch (Exception e) {
                                errorMessage = "Network error: " + error.getMessage();
                            }
                        }
                        Toast.makeText(NetworkActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

