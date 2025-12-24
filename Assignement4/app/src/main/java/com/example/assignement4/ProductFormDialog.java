package com.example.assignement4;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductFormDialog extends DialogFragment {
    private TextInputEditText editTextName;
    private TextInputEditText editTextDescription;
    private TextInputEditText editTextPrice;
    private Spinner spinnerCategory;
    private Button buttonSubmit;
    private Button buttonCancel;
    private TextView textViewMessage;
    private TextView textViewTitle;
    private ProgressBar progressBar;
    private RequestQueue requestQueue;
    private OnProductCreatedListener listener;
    private List<Category> categories;
    private Product editingProduct;
    // Base URL Configuration:
    // For Android Emulator: use "http://10.0.2.2:3000/api"
    // For Physical Device with USB Tethering: use your computer's IP address
    // Find your IP: Run "ipconfig" in Command Prompt, look for IPv4 Address
    // Common tethering IPs: 192.168.42.1, 192.168.43.1, 192.168.137.1
    // Current IP: 10.144.195.113 (Ethernet 7) or 172.31.118.74 (Wi-Fi)
    private static final String BASE_URL = "http://10.144.195.113:3000/api";
    private static final String PRODUCTS_URL = BASE_URL + "/products";
    private static final String CATEGORIES_URL = BASE_URL + "/categories";

    public interface OnProductCreatedListener {
        void onProductCreated(Product product);
    }

    public void setOnProductCreatedListener(OnProductCreatedListener listener) {
        this.listener = listener;
    }

    public void setEditingProduct(Product product) {
        this.editingProduct = product;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_product_form, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        textViewMessage = view.findViewById(R.id.textViewMessage);
        textViewTitle = view.findViewById(R.id.textViewTitle);
        progressBar = view.findViewById(R.id.progressBar);

        requestQueue = Volley.newRequestQueue(requireContext());
        categories = new ArrayList<>();

        // Load categories
        loadCategories();

        buttonSubmit.setOnClickListener(v -> createProduct());
        buttonCancel.setOnClickListener(v -> dismiss());

        // If editing, populate fields
        if (editingProduct != null) {
            textViewTitle.setText("Edit Product");
            editTextName.setText(editingProduct.getName());
            editTextDescription.setText(editingProduct.getDescription());
            editTextPrice.setText(String.valueOf(editingProduct.getPrice()));
            buttonSubmit.setText("Update");
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void loadCategories() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                CATEGORIES_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        categories.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Category category = new Category();
                                category.setId(jsonObject.getInt("id"));
                                category.setName(jsonObject.getString("name"));
                                category.setDescription(jsonObject.optString("description", null));
                                categories.add(category);
                            }

                            // Populate spinner
                            ArrayAdapter<Category> adapter = new ArrayAdapter<>(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    categories
                            );
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCategory.setAdapter(adapter);

                            // If editing, select the product's category
                            if (editingProduct != null) {
                                for (int i = 0; i < categories.size(); i++) {
                                    if (categories.get(i).getId() == editingProduct.getCategory_id()) {
                                        spinnerCategory.setSelection(i);
                                        break;
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            showMessage("Error loading categories: " + e.getMessage(), true);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = "Error loading categories";
                        if (error.getMessage() != null) {
                            errorMsg += ": " + error.getMessage();
                        } else {
                            errorMsg += ": Cannot reach server at " + CATEGORIES_URL;
                        }
                        showMessage(errorMsg, true);
                    }
                }
        );

        requestQueue.add(request);
    }

    private void createProduct() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();

        if (name.isEmpty()) {
            showMessage("Please enter product name", true);
            return;
        }

        if (priceStr.isEmpty()) {
            showMessage("Please enter price", true);
            return;
        }

        if (spinnerCategory.getSelectedItem() == null) {
            showMessage("Please select a category", true);
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
            int categoryId = selectedCategory.getId();

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name);
            jsonBody.put("description", description.isEmpty() ? null : description);
            jsonBody.put("price", price);
            jsonBody.put("category_id", categoryId);

            String url = editingProduct != null ? PRODUCTS_URL + "/" + editingProduct.getId() : PRODUCTS_URL;
            int method = editingProduct != null ? Request.Method.PUT : Request.Method.POST;

            JsonObjectRequest request = new JsonObjectRequest(
                    method,
                    url,
                    jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Product product;
                            try {
                                if (editingProduct != null) {
                                    // For PUT, server returns { message: "..." }
                                    // Use the edited values to update the product
                                    product = editingProduct;
                                    product.setName(editTextName.getText().toString().trim());
                                    product.setDescription(editTextDescription.getText().toString().trim());
                                    product.setPrice(Double.parseDouble(editTextPrice.getText().toString().trim()));
                                    Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
                                    if (selectedCategory != null) {
                                        product.setCategory_id(selectedCategory.getId());
                                        product.setCategory_name(selectedCategory.getName());
                                    }
                                } else {
                                    // For POST, server returns the created product with all fields
                                    product = new Product();
                                    product.setId(response.getInt("id"));
                                    product.setName(response.getString("name"));
                                    product.setDescription(response.optString("description", null));
                                    product.setPrice(response.getDouble("price"));
                                    product.setCategory_id(response.getInt("category_id"));
                                    product.setCategory_name(response.optString("category_name", null));
                                }

                                setLoading(false);
                                showMessage("Product " + (editingProduct != null ? "updated" : "created") + " successfully!", false);
                                
                                if (listener != null) {
                                    listener.onProductCreated(product);
                                }

                                // Dismiss after a short delay
                                buttonSubmit.postDelayed(() -> dismiss(), 1000);
                            } catch (JSONException e) {
                                setLoading(false);
                                showMessage("Error parsing response: " + e.getMessage(), true);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errorMessage = "Error " + (editingProduct != null ? "updating" : "creating") + " product";
                            if (error.networkResponse != null) {
                                try {
                                    String responseBody = new String(error.networkResponse.data, "utf-8");
                                    JSONObject jsonObject = new JSONObject(responseBody);
                                    errorMessage = jsonObject.optString("error", errorMessage);
                                } catch (Exception e) {
                                    errorMessage = "Network error: " + error.getMessage();
                                }
                            } else {
                                errorMessage = "Network error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                            }
                            setLoading(false);
                            showMessage(errorMessage, true);
                        }
                    }
            );

            requestQueue.add(request);
            setLoading(true);
            showMessage((editingProduct != null ? "Updating" : "Creating") + " product...", false);

        } catch (NumberFormatException e) {
            showMessage("Invalid price", true);
        } catch (JSONException e) {
            showMessage("Error creating request: " + e.getMessage(), true);
        }
    }

    private void showMessage(String message, boolean isError) {
        textViewMessage.setText(message);
        textViewMessage.setVisibility(View.VISIBLE);
        textViewMessage.setTextColor(isError ?
                getResources().getColor(android.R.color.holo_red_dark) :
                getResources().getColor(android.R.color.holo_green_dark));

        if (!isError) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        buttonSubmit.setEnabled(!loading);
        buttonCancel.setEnabled(!loading);
        editTextName.setEnabled(!loading);
        editTextDescription.setEnabled(!loading);
        editTextPrice.setEnabled(!loading);
        spinnerCategory.setEnabled(!loading);
    }
}

