package com.example.assignement4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductFormFragment extends Fragment {
    private TextInputEditText editTextName;
    private TextInputEditText editTextDescription;
    private TextInputEditText editTextPrice;
    private TextInputEditText editTextCategoryId;
    private Button buttonSubmit;
    private TextView textViewMessage;
    private RequestQueue requestQueue;
    private OnProductCreatedListener listener;

    // Base URL - Change this to your computer's IP when using tethering
    // For Android Emulator: use 10.0.2.2 instead of localhost
    // For physical device with tethering: use your computer's IP (e.g., 192.168.43.1)
    // Current IP: 10.144.195.113 (Ethernet 7) or 172.31.118.74 (Wi-Fi)
    private static final String BASE_URL = "http://10.144.195.113:3000/api/products";

    public interface OnProductCreatedListener {
        void onProductCreated(Product product);
    }

    public void setOnProductCreatedListener(OnProductCreatedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_form, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        editTextCategoryId = view.findViewById(R.id.editTextCategoryId);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        textViewMessage = view.findViewById(R.id.textViewMessage);

        requestQueue = Volley.newRequestQueue(requireContext());

        buttonSubmit.setOnClickListener(v -> createProduct());

        return view;
    }

    private void createProduct() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String categoryIdStr = editTextCategoryId.getText().toString().trim();

        if (name.isEmpty()) {
            showMessage("Please enter product name", true);
            return;
        }

        if (priceStr.isEmpty()) {
            showMessage("Please enter price", true);
            return;
        }

        if (categoryIdStr.isEmpty()) {
            showMessage("Please enter category ID", true);
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int categoryId = Integer.parseInt(categoryIdStr);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name);
            jsonBody.put("description", description.isEmpty() ? null : description);
            jsonBody.put("price", price);
            jsonBody.put("category_id", categoryId);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL,
                    jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Product product = new Product();
                                product.setId(response.getInt("id"));
                                product.setName(response.getString("name"));
                                product.setDescription(response.optString("description", null));
                                product.setPrice(response.getDouble("price"));
                                product.setCategory_id(response.getInt("category_id"));
                                product.setCategory_name(response.optString("category_name", null));

                                showMessage("Product created successfully!", false);
                                clearForm();

                                if (listener != null) {
                                    listener.onProductCreated(product);
                                }
                            } catch (JSONException e) {
                                showMessage("Error parsing response: " + e.getMessage(), true);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errorMessage = "Error creating product";
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
                            showMessage(errorMessage, true);
                        }
                    }
            );

            requestQueue.add(request);
            showMessage("Creating product...", false);

        } catch (NumberFormatException e) {
            showMessage("Invalid price or category ID", true);
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

    private void clearForm() {
        editTextName.setText("");
        editTextDescription.setText("");
        editTextPrice.setText("");
        editTextCategoryId.setText("1");
    }
}

