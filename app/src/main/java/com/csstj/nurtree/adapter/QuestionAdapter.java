package com.csstj.nurtree.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.csstj.nurtree.R;
import com.csstj.nurtree.common.util.ColorUtil;
import com.csstj.nurtree.data.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<Question> questions;
    private List<String> selectedAnswers;
    private int currentQuestionIndex;
    private boolean isHiddenQuestionTip = true;

    public QuestionAdapter(List<Question> questions, int currentQuestionIndex) {
        this.questions = questions;
        this.selectedAnswers = new ArrayList<>(Collections.nCopies(questions.size(), null));
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public void setHiddenQuestionTip(boolean hidden) {
        this.isHiddenQuestionTip = hidden;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        if(questions.size()>0){
            Question question = questions.get(currentQuestionIndex);
            holder.questionNameView.setText(question.getQuestionName());
            holder.answerTextView.setText(question.getCorrectAnswer());
            holder.noteTextView.setText(question.getNote());

            if (isHiddenQuestionTip) {
                holder.questionTip.setVisibility(View.GONE);
            } else {
                holder.questionTip.setVisibility(View.VISIBLE);
            }

            // Clear previous options
            holder.optionsRadioGroup.removeAllViews();

            // Shuffle options
            List<String> options = new ArrayList<>();
            options.addAll(question.getOptions());

            // Add options to RadioGroup
            for (String option : options) {
                RadioButton radioButton = createRadioButton(holder,option);
                holder.optionsRadioGroup.addView(radioButton);
            }

            // Set selected answer if exists
            if (selectedAnswers.get(currentQuestionIndex) != null) {
                for (int i = 0; i < holder.optionsRadioGroup.getChildCount(); i++) {
                    RadioButton radioButton = (RadioButton) holder.optionsRadioGroup.getChildAt(i);
                    if (radioButton.getText().toString().equals(selectedAnswers.get(currentQuestionIndex))) {
                        radioButton.setChecked(true);
                        break;
                    }
                }
            }

            // Save selected answer
            holder.optionsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton selectedRadioButton = group.findViewById(checkedId);
                    //禁用RadioGroup
                    disabledRadioGroup(group);
                    selectedAnswers.set(currentQuestionIndex, selectedRadioButton.getText().toString());
                    holder.selectedAnswerTextView.setText(selectedRadioButton.getText().toString());
                    if(!selectedRadioButton.getText().toString().equals(holder.answerTextView.getText().toString())){
                        holder.selectedAnswerTextView.setTextColor(ColorUtil.hex2Int("#FF0000"));
                    }else {
                        int customColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.blue_60);
                        holder.selectedAnswerTextView.setTextColor(customColor);
                    }
                    holder.questionTip.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 1; // Only show one question at a time
    }

    public List<String> getSelectedAnswers() {
        return selectedAnswers;
    }

    public void setSelectedAnswers(int questionNum) {
        this.selectedAnswers = new ArrayList<>(Collections.nCopies(questionNum, null));
    }

    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
        notifyDataSetChanged();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionNameView;
        RadioGroup optionsRadioGroup;
        TextView answerTextView;
        TextView selectedAnswerTextView;
        TextView noteTextView;
        LinearLayout questionTip;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionNameView = itemView.findViewById(R.id.questionNameView);
            optionsRadioGroup = itemView.findViewById(R.id.optionsRadioGroup);
            answerTextView = itemView.findViewById(R.id.answerTextView);
            selectedAnswerTextView = itemView.findViewById(R.id.selectedAnswerTextView);
            noteTextView = itemView.findViewById(R.id.noteTextView);
            questionTip = itemView.findViewById(R.id.question_tip);
        }
    }

    private RadioButton createRadioButton(@NonNull QuestionViewHolder holder,String text) {
        // 使用 ContextThemeWrapper 应用样式
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(holder.itemView.getContext(), R.style.RadioButtonStyle);
        LayoutInflater inflater = LayoutInflater.from(contextThemeWrapper);

        // 创建 RadioButton
        RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.custom_radio_button, null);
        radioButton.setText(text);
        setRadioButtonStyle(holder.itemView.getContext(),radioButton,10,40);
        return radioButton;
    }

    private void setRadioButtonStyle(Context context,RadioButton radioButton, int marginTopDp, int layoutHeightDp) {
        // 将 dp 转换为 px
        int marginTopPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                marginTopDp,
                context.getResources().getDisplayMetrics()
        );

        int layoutHeightPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                layoutHeightDp,
                context.getResources().getDisplayMetrics()
        );

        // 设置宽度为全屏
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, // 宽度占满全屏
                layoutHeightPx  // 高度根据内容自适应
        );
        // 设置 layout_marginTop
        params.setMargins(0, marginTopPx, 0, 0); // 设置 marginTop
        radioButton.setLayoutParams(params);
        // 设置 paddingLeft
        setPaddingLeft(context,radioButton,25);
    }

    private void setPaddingLeft(Context context, RadioButton radioButton, int paddingLeftDp) {
        // 将 dp 转换为 px
        int paddingLeftPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                paddingLeftDp,
                context.getResources().getDisplayMetrics()
        );

        // 设置 paddingLeft
        radioButton.setPadding(
                paddingLeftPx, // left
                radioButton.getPaddingTop(), // top
                radioButton.getPaddingRight(), // right
                radioButton.getPaddingBottom() // bottom
        );
    }

    private void disabledRadioGroup(RadioGroup group){
        // 获取RadioGroup中所有的RadioButton
        int radioButtonCount = group.getChildCount();
        for (int i = 0; i < radioButtonCount; i++) {
            RadioButton radioButton = (RadioButton) group.getChildAt(i);
            // 禁用所有的RadioButton，防止再次选择
            radioButton.setEnabled(false);
        }
    }
}