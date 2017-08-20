package jason.tuchler.com.popularmovies;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.view.LayoutInflater;
import java.util.ArrayList;

public class AdapterReviews extends BaseAdapter
{
    ArrayList<String> review;
    ArrayList<String> name;

    Context context;

    public  AdapterReviews(ArrayList<String> review, ArrayList<String> name, Context context){

        this.review = review;
        this.name = name;
        this.context = context;
    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AdapterReviews.ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.activity_review_holder, null);
            viewHolder = new AdapterReviews.ViewHolder();

            viewHolder.lblReview = (TextView) convertView
                    .findViewById(R.id.lbl_review);
            viewHolder.lblName = (TextView) convertView
                    .findViewById(R.id.lbl_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (AdapterReviews.ViewHolder) convertView.getTag();
        }

        viewHolder.lblReview.setText(review.get(position));
        viewHolder.lblName.setText(name.get(position));

        return convertView;
    }

    public class ViewHolder {
        public TextView lblReview;
        public TextView lblName;

    }
}