package com.mindcrafts.posapp.activitiese.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ProductViewHolder> {

    private final Context context;
    private List<InventoryResponse.Product> productList;
    InventoryCallbacks callbacks;


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
        //glide
        Glide.with(context)
                .load(product.getsPath())
                .placeholder(R.drawable.placeholder) // optional
                .error(R.drawable.placeholder) // optional
                .into(holder.imgProduct);

        holder.btnAddToCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 cartProduct.setId(product.getId());
                 cartProduct.setName(product.getName());
                 cartProduct.setPrice(product.getMinPrice());
                 cartProduct.setCount(1);
                 holder.counter.setText(String.valueOf(cartProduct.getCount()));
                 holder.btnAddToCard.setVisibility(View.GONE);
                 holder.addToCartContainer.setVisibility(View.VISIBLE);
                 callbacks.getSingleProduct(cartProduct);
                //Toast.makeText(context, "BtnClick", Toast.LENGTH_SHORT).show();
            }
        });
        holder.counter.setText(String.valueOf(cartProduct.getCount()));
        holder.addItemToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartProduct.setId(product.getId());
                cartProduct.setName(product.getName());
                cartProduct.setPrice(product.getMinPrice());
                cartProduct.setCount(cartProduct.getCount() + 1);
                holder.counter.setText(String.valueOf(cartProduct.getCount()));
                callbacks.getSingleProduct(cartProduct);
            }
        });
        holder.removeItemFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartProduct.setId(product.getId());
                cartProduct.setName(product.getName());
                cartProduct.setPrice(product.getMinPrice());
                cartProduct.setCount(cartProduct.getCount() - 1);
                holder.counter.setText(String.valueOf(cartProduct.getCount()));
                if(cartProduct.getCount() == 0){
                    holder.addToCartContainer.setVisibility(View.GONE);
                    holder.btnAddToCard.setVisibility(View.VISIBLE);
                }
                callbacks.getSingleProduct(cartProduct);
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
            removeItemFromCart = itemView.findViewById(R.id.rmeove_item_container);
            counter = itemView.findViewById(R.id.itemCount);
            //imgProduct = itemView.findViewById(R.id.imgProduct);
            //txtProductName = itemView.findViewById(R.id.txtProductName);
            //txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
        }
    }
}

