package jason.tuchler.com.popularmovies;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.view.LayoutInflater;
import java.util.ArrayList;

public class AdapterTrailers extends BaseAdapter
{
    ArrayList<String> name;
    Context context;

    public  AdapterTrailers(ArrayList<String> name, Context context){

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
        AdapterTrailers.ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.activity_trailer_holder, null);
            viewHolder = new AdapterTrailers.ViewHolder();

            viewHolder.lblName = (TextView ) convertView
                    .findViewById(R.id.lbl_trailer);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (AdapterTrailers.ViewHolder) convertView.getTag();
        }

        viewHolder.lblName.setText(name.get(position));

        return convertView;
    }

    public class ViewHolder {
        public TextView lblName;
    }
}
