package com.mindcrafts.posapp.activitiese.adapters;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.Double.valueOf;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mindcrafts.posapp.R;
import com.mindcrafts.posapp.activitiese.adapters.callbacks.InventoryCallbacks;
import com.mindcrafts.posapp.activitiese.inventory.models.CartProduct;
import com.mindcrafts.posapp.activitiese.inventory.models.InventoryResponse;
import com.mindcrafts.posapp.activitiese.inventory.models.Product;

import java.nio.DoubleBuffer;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ProductViewHolder> {

    private final Context context;
    private List<InventoryResponse.Product> productList;
    InventoryCallbacks callbacks;
    private int lastPosition = -1;
    public InventoryAdapter(Context context, List<InventoryResponse.Product> productList, InventoryCallbacks callback) {
        this.context = context;
        this.productList = productList;
        this.callbacks = callback;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inventroy_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
       CartProduct  cartProduct = new CartProduct();
        InventoryResponse.Product product = productList.get(position);
        holder.txtProductName.setText(product.getName());
        holder.ItemPrice.setText(String.valueOf(product.getMinPrice()));
        holder.maxPrice.setHint(String.valueOf(product.getLowerCap()) +" - "+ String.valueOf(product.getMaxPrice()));
        //glide
        Glide.with(context)
                .load(product.getsPath())
                .placeholder(R.drawable.placeholder) // optional
                .error(R.drawable.placeholder) // optional
                .into(holder.imgProduct);

        if (lastPosition==position){
            holder.addToCartContainer.setVisibility(VISIBLE);
            holder.btnAddToCard.setVisibility(GONE);
        }else {
            holder.addToCartContainer.setVisibility(GONE);
            holder.btnAddToCard.setVisibility(VISIBLE);
        }
        Log.d("lastPosition", "onBindViewHolder: "+lastPosition);
        holder.btnAddToCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastPosition = position;
                 String amount  = holder.maxPrice.getText().toString();
                 if(amount.isEmpty()){
                     cartProduct.setId(product.getId());
                     cartProduct.setName(product.getName());
                     cartProduct.setPrice(product.getMinPrice());
                     cartProduct.setCount(1);
                     holder.counter.setText(String.valueOf(cartProduct.getCount()));
                     holder.btnAddToCard.setVisibility(GONE);
                     holder.addToCartContainer.setVisibility(VISIBLE);
                     callbacks.getSingleProduct(cartProduct);
                     //Toast.makeText(context, "BtnClick", Toast.LENGTH_SHORT).show();
                 }else{
                    if(Double.parseDouble(amount) > product.getMaxPrice() || Double.parseDouble(amount) < product.getLowerCap()){
                       Toast.makeText(context, "Amount must be in maximum or minimum amount range", Toast.LENGTH_SHORT).show();
                    }else{
                        cartProduct.setId(product.getId());
                        cartProduct.setName(product.getName());
                        cartProduct.setPrice(Double.parseDouble(amount));
                        cartProduct.setCount(1);
                        holder.counter.setText(String.valueOf(cartProduct.getCount()));
                        holder.btnAddToCard.setVisibility(GONE);
                        holder.addToCartContainer.setVisibility(VISIBLE);
                        callbacks.getSingleProduct(cartProduct);
                    }
                 }

            }
        });
        holder.counter.setText(String.valueOf(cartProduct.getCount()));
        holder.addItemToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount  = holder.maxPrice.getText().toString();
                if(amount.isEmpty()) {
                    cartProduct.setId(product.getId());
                    cartProduct.setName(product.getName());
                    cartProduct.setPrice(product.getMinPrice());
                    cartProduct.setCount(cartProduct.getCount() + 1);
                    holder.counter.setText(String.valueOf(cartProduct.getCount()));
                    callbacks.getSingleProduct(cartProduct);
                }else{
                    if(Double.parseDouble(amount) > product.getMaxPrice() || Double.parseDouble(amount) < product.getLowerCap() ){
                        Toast.makeText(context, "Amount must be in maximum or minimum amount range", Toast.LENGTH_SHORT).show();
                    }else{
                        cartProduct.setId(product.getId());
                        cartProduct.setName(product.getName());
                        cartProduct.setPrice(Double.parseDouble(amount));
                        cartProduct.setCount(cartProduct.getCount() + 1);
                        holder.counter.setText(String.valueOf(cartProduct.getCount()));
                        callbacks.getSingleProduct(cartProduct);
                    }
                }
            }
        });
        holder.removeItemFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount  = holder.maxPrice.getText().toString();
                if(amount.isEmpty()) {
                    cartProduct.setId(product.getId());
                    cartProduct.setName(product.getName());
                    cartProduct.setPrice(product.getMinPrice());
                    if(cartProduct.getCount() != 0){
                        cartProduct.setCount(cartProduct.getCount() - 1);
                    }
                    holder.counter.setText(String.valueOf(cartProduct.getCount()));
                    if (cartProduct.getCount() == 0) {
                        holder.addToCartContainer.setVisibility(GONE);
                        holder.btnAddToCard.setVisibility(VISIBLE);
                    }
                    callbacks.getSingleProduct(cartProduct);
                }else{
                    if(Double.parseDouble(amount) > product.getMaxPrice() || Double.parseDouble(amount) < product.getLowerCap() ){
                        Toast.makeText(context, "Amount must be in maximum or minimum amount range", Toast.LENGTH_SHORT).show();
                    }else{
                        cartProduct.setId(product.getId());
                        cartProduct.setName(product.getName());
                        cartProduct.setPrice(Double.parseDouble(amount));
                        if(cartProduct.getCount() != 0){
                            cartProduct.setCount(cartProduct.getCount() - 1);
                        }
                        holder.counter.setText(String.valueOf(cartProduct.getCount()));
                        if (cartProduct.getCount() == 0) {
                            lastPosition=-1;
                            
                            holder.addToCartContainer.setVisibility(GONE);
                            holder.btnAddToCard.setVisibility(VISIBLE);
                        }
                        callbacks.getSingleProduct(cartProduct);
                    }
                }
            }
        });
        //
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName;
        TextView ItemPrice;

        EditText maxPrice;

        ConstraintLayout btnAddToCard;

        ConstraintLayout addToCartContainer;
        ConstraintLayout addItemToCart;
        ConstraintLayout removeItemFromCart;
        TextView counter;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
             txtProductName = itemView.findViewById(R.id.itemName);
             ItemPrice = itemView.findViewById(R.id.price);
             imgProduct = itemView.findViewById(R.id.productImage);
             btnAddToCard= itemView.findViewById(R.id.addToCart);
            addToCartContainer = itemView.findViewById(R.id.constraintLayout25);
            addItemToCart = itemView.findViewById(R.id.itemAdd);
            maxPrice = itemView.findViewById(R.id.amountUnit);
            removeItemFromCart = itemView.findViewById(R.id.rmeove_item_container);
            counter = itemView.findViewById(R.id.itemCount);
            //imgProduct = itemView.findViewById(R.id.imgProduct);
            //txtProductName = itemView.findViewById(R.id.txtProductName);
            //txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
        }
    }
}

