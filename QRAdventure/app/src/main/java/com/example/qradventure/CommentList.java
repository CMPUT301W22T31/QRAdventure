package com.example.qradventure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CommentList extends ArrayAdapter<Comment> {

    private ArrayList<Comment> comments;
    private Context context;

    public CommentList(Context context, ArrayList<Comment> comments){
        super(context,0, comments);
        this.comments = comments;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View view = convertView;
            if(view == null){
                view = LayoutInflater.from(context).inflate(R.layout.content_comment, parent,false);
            }

            Comment comment = comments.get(position);
            TextView commentAuthor = view.findViewById(R.id.text_comment_author);
            TextView commentText = view.findViewById(R.id.text_comment);
            EditText commentEntered = view.findViewById(R.id.editText_comment);

            String usernameDisplay = comment.getAuthorUsername()+":";

            commentAuthor.setText(usernameDisplay);
            commentText.setText(comment.getText());
            return view;

    }
}
