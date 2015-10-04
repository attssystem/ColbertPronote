package pronote.colbert.fliife.com.colbertpronote;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private String[] mMatter;
    private String[] mContent;
    private String[] mClass;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextViewTitle;
        public TextView mTextViewContent;
        public TextView mTextViewClass;
        public ViewHolder(RelativeLayout v) {
            super(v);
            mTextViewTitle = (TextView) v.findViewById(R.id.title_text);
            mTextViewContent = (TextView) v.findViewById(R.id.content_text);
            mTextViewClass = (TextView) v.findViewById(R.id.top_right_text);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(String[] myMatter, String[] myContent, String[] myClass) {
        mMatter = myMatter;
        mContent = myContent;
        mClass = myClass;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_base, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextViewTitle.setText(mMatter[position]);
        holder.mTextViewContent.setText(mContent[position]);
        holder.mTextViewClass.setText(mClass[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mMatter.length;
    }
}