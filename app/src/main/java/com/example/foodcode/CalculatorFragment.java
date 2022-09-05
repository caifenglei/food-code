package com.example.foodcode;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A calculator
 */
public class CalculatorFragment extends Fragment {

    Double calResult = 0.00;
    String numberStack = "";
    View fragmentView;
    TextView calResultView;
    ArrayList<Double> calElements = new ArrayList<>(2);
    int activeOperator;

    public CalculatorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_calculator, container, false);
        calResultView = fragmentView.findViewById(R.id.amountResult);

        Button keyOne = fragmentView.findViewById(R.id.keyOne);
        Button keyTwo = fragmentView.findViewById(R.id.keyTwo);
        Button keyThree = fragmentView.findViewById(R.id.keyThree);
        Button keyFour = fragmentView.findViewById(R.id.keyFour);
        Button keyFive = fragmentView.findViewById(R.id.keyFive);
        Button keySix = fragmentView.findViewById(R.id.keySix);
        Button keySeven = fragmentView.findViewById(R.id.keySeven);
        Button keyEight = fragmentView.findViewById(R.id.keyEight);
        Button keyNine = fragmentView.findViewById(R.id.keyNine);
        Button keyZero = fragmentView.findViewById(R.id.keyZero);
        Button keyPoint = fragmentView.findViewById(R.id.keyPoint);
        AppCompatImageButton keyDelete = fragmentView.findViewById(R.id.keyDelete);
        Button keyPlus = fragmentView.findViewById(R.id.keyPlus);
        Button keyDeduct = fragmentView.findViewById(R.id.keyDeduct);
        Button keyMultiply = fragmentView.findViewById(R.id.keyMultiply);
        Button keyDivide = fragmentView.findViewById(R.id.keyDivide);
        Button keyCashier = fragmentView.findViewById(R.id.keyCashier);

        keyOne.setOnClickListener(onCalculatorKeyClick);
        keyTwo.setOnClickListener(onCalculatorKeyClick);
        keyThree.setOnClickListener(onCalculatorKeyClick);
        keyFour.setOnClickListener(onCalculatorKeyClick);
        keyFive.setOnClickListener(onCalculatorKeyClick);
        keySix.setOnClickListener(onCalculatorKeyClick);
        keySeven.setOnClickListener(onCalculatorKeyClick);
        keyEight.setOnClickListener(onCalculatorKeyClick);
        keyNine.setOnClickListener(onCalculatorKeyClick);
        keyZero.setOnClickListener(onCalculatorKeyClick);
        keyPoint.setOnClickListener(onCalculatorKeyClick);
        keyDelete.setOnClickListener(onCalculatorKeyClick);
        keyPlus.setOnClickListener(onCalculatorKeyClick);
        keyDeduct.setOnClickListener(onCalculatorKeyClick);
        keyMultiply.setOnClickListener(onCalculatorKeyClick);
        keyDivide.setOnClickListener(onCalculatorKeyClick);
        keyCashier.setOnClickListener(onCalculatorKeyClick);

        return fragmentView;
    }

    private View.OnClickListener onCalculatorKeyClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.keyOne:
                case R.id.keyTwo:
                case R.id.keyThree:
                case R.id.keyFour:
                case R.id.keyFive:
                case R.id.keySix:
                case R.id.keySeven:
                case R.id.keyEight:
                case R.id.keyNine:
                case R.id.keyZero:
                    Button b = (Button) view;
                    tapNumber(b.getText().toString());
                    break;
                case R.id.keyPoint:
                    tapPoint();
                    break;
                case R.id.keyPlus:
                case R.id.keyDeduct:
                case R.id.keyMultiply:
                case R.id.keyDivide:
                    tapOperator(viewId);
                    break;
                case R.id.keyDelete:
                    tapClear();
                    break;
                case R.id.keyCashier:
                    tapCashier();
                    break;
            }
        }
    };

    private void tapNumber(String nbr) {
        if (Objects.equals(numberStack, "")) {
            numberStack = nbr;
        } else {
            if(numberStack.length() < 6) {
                numberStack = numberStack + nbr;
            }
        }
        formatResultValue(Double.parseDouble(numberStack));
    }

    private void tapPoint() {
        if (Objects.equals(numberStack, "")) {
            numberStack = "0.";
        } else {
            if (!numberStack.endsWith(".")) {
                numberStack = numberStack + '.';
            }
        }
    }

    private void tapOperator(int opt) {
        int elementCount = calElements.size();
        if (elementCount == 0) {
            if (!Objects.equals(numberStack, "")) {
                calElements.add(Double.parseDouble(numberStack));
                numberStack = "";
                activeOperator = opt;
            }
        } else if (elementCount == 1) {
            if (!Objects.equals(numberStack, "")) {
                calElements.add(Double.parseDouble(numberStack));
                if(calculateResult(activeOperator)) {
                    calElements.add(calResult);
                    activeOperator = opt;
                }
            }
        } else {
            //2 factors, then calculate the result
            calculateResult(opt);
        }
    }

    // clear all
    private void tapClear() {
        numberStack = "";
        calElements.clear();
        calResult = 0.00;
        calResultView.setText("0.00");
    }

    private void tapCashier() {
        int factorCount = calElements.size();
        Double moneyToPay = 0.00;
        if (factorCount == 1 && calResult > 0) {

            moneyToPay = calResult;
        } else if (factorCount == 2) {
            if (calculateResult(activeOperator)) {
                moneyToPay = calResult;
            }
        }
        if (moneyToPay > 0) {
            Log.i("cashier", String.valueOf(moneyToPay));
        } else {
            Log.i("cashier", "no Money to pay");
        }
    }

    private boolean calculateResult(int opt) {

        boolean ok = true;
        if (opt == R.id.keyPlus) {
            calResult = calElements.get(0) + calElements.get(1);
        } else if (opt == R.id.keyDeduct) {
            calResult = calElements.get(0) - calElements.get(1);
        } else if (opt == R.id.keyMultiply) {
            calResult = calElements.get(0) * calElements.get(1);
        } else if (opt == R.id.keyDivide) {
            if (calElements.get(1) == 0) {
                calResultView.setText("错误");
                ok = false;
            } else {
                calResult = calElements.get(0) / calElements.get(1);
            }
        }

        if (ok) {
            numberStack = "";
            calElements.clear();
            formatResultValue(calResult);
        }

        return ok;
    }

    private String formatResultValue(Double value){
        BigDecimal bd = new BigDecimal(value);
        calResult = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); //四舍五入

        DecimalFormat df = new DecimalFormat("0.00");
        String format = df.format(value);
        calResultView.setText(format);

//        Log.i("FFF", String.valueOf(value) + "," + String.valueOf(bd) + "," + String.valueOf(calResult) + "|" + format);
        return format;
    }
}