package com.supermarket.calculator.adapter;

import com.supermarket.calculator.models.CartItem;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.supermarket.calculator.R;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends BaseAdapter {

    public interface CartListener {
        void onIncrement(int position);
        void onDecrement(int position);
        void onRemove(int position);
        void onItemClick(int position);
        void onNameChanged(int position, String name);
    }

    private final Context context;
    private final List<CartItem> items;
    private final CartListener listener;
    private final NumberFormat currencyFormat;
    private int editingPosition = -1;
    private int currentTheme = 0;
    private static final int THEME_DARK = 1;

    public CartAdapter(Context context, List<CartItem> items, CartListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    }

    public void setTheme(int theme) {
        this.currentTheme = theme;
    }

    private boolean isDarkTheme() {
        return currentTheme == THEME_DARK;
    }

    private int themeColor(int lightRes, int darkRes) {
        return context.getResources().getColor(isDarkTheme() ? darkRes : lightRes);
    }

    public void setEditingPosition(int pos) {
        if (editingPosition != pos) {
            int oldPos = editingPosition;
            editingPosition = pos;
            if (oldPos >= 0) notifyDataSetChanged();
            if (pos >= 0) notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() { return items.size(); }

    @Override
    public Object getItem(int position) { return items.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                .inflate(R.layout.cart_item, parent, false);
        }

        CartItem item = items.get(position);

        EditText nameEt = convertView.findViewById(R.id.itemName);
        TextView qtyTv = convertView.findViewById(R.id.itemQty);
        TextView unitTv = convertView.findViewById(R.id.itemUnitPrice);
        TextView totalTv = convertView.findViewById(R.id.itemTotalPrice);
        Button decBtn = convertView.findViewById(R.id.decBtn);
        Button incBtn = convertView.findViewById(R.id.incBtn);
        ImageButton removeBtn = convertView.findViewById(R.id.removeBtn);

        if (position == editingPosition) {
            nameEt.setEnabled(true);
            nameEt.setFocusable(true);
            nameEt.setFocusableInTouchMode(true);
            nameEt.setClickable(true);
            nameEt.setCursorVisible(true);
            nameEt.setBackgroundResource(R.drawable.bg_input);
            nameEt.setPadding(4, 0, 4, 0);

            convertView.setBackgroundColor(themeColor(R.color.highlightBg, R.color.darkHighlightBg));
        } else {
            nameEt.setEnabled(false);
            nameEt.setFocusable(false);
            nameEt.setFocusableInTouchMode(false);
            nameEt.setClickable(false);
            nameEt.setCursorVisible(false);
            nameEt.setBackgroundResource(0);
            nameEt.setPadding(0, 0, 0, 0);
            nameEt.setTextColor(themeColor(R.color.textPrimary, R.color.darkTextPrimary));

            int rowColorRes = position % 2 == 0 ? R.color.rowEven : R.color.rowOdd;
            int darkRowColorRes = position % 2 == 0 ? R.color.darkRowEven : R.color.darkRowOdd;
            convertView.setBackgroundColor(themeColor(rowColorRes, darkRowColorRes));
        }

        Object tag = nameEt.getTag();
        if (tag instanceof TextWatcher) nameEt.removeTextChangedListener((TextWatcher) tag);

        nameEt.setText(item.getName());

        TextWatcher watcher = new TextWatcher() {
            final int pos = position;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null && pos == editingPosition) {
                    listener.onNameChanged(pos, s.toString());
                }
            }
        };
        nameEt.addTextChangedListener(watcher);
        nameEt.setTag(watcher);

        qtyTv.setText(String.valueOf(item.getQuantity()));
        unitTv.setText(currencyFormat.format(item.getUnitPrice()));
        totalTv.setText(currencyFormat.format(item.getTotal()));

        decBtn.setOnClickListener(v -> {
            if (listener != null) listener.onDecrement(position);
        });

        incBtn.setOnClickListener(v -> {
            if (listener != null) listener.onIncrement(position);
        });

        removeBtn.setOnClickListener(v -> {
            if (listener != null) listener.onRemove(position);
        });

        convertView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);
        });

        return convertView;
    }
}
