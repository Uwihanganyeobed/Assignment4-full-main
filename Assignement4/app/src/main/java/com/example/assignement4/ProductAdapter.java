package com.example.assignement4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private OnProductActionListener listener;

    public interface OnProductActionListener {
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }

    public ProductAdapter() {
        this.products = new ArrayList<>();
    }

    public void setOnProductActionListener(OnProductActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public void addProduct(Product product) {
        this.products.add(0, product);
        notifyItemInserted(0);
    }

    public void removeProduct(Product product) {
        int position = products.indexOf(product);
        if (position != -1) {
            products.remove(position);
            notifyItemRemoved(position);
        }
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewProductName;
        private TextView textViewProductDescription;
        private TextView textViewPrice;
        private TextView textViewCategory;
        private TextView textViewId;
        private ImageButton buttonEdit;
        private ImageButton buttonDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductDescription = itemView.findViewById(R.id.textViewProductDescription);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewId = itemView.findViewById(R.id.textViewId);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }

        public void bind(Product product) {
            textViewProductName.setText(product.getName());
            textViewProductDescription.setText(product.getDescription() != null ? product.getDescription() : "No description");
            textViewPrice.setText(String.format("$%.2f", product.getPrice()));
            textViewCategory.setText(product.getCategory_name() != null ? product.getCategory_name() : "N/A");
            textViewId.setText("ID: " + product.getId());

            buttonEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(product);
                }
            });

            buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(product);
                }
            });
        }
    }
}

