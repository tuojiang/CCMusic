package oyh.ccmusic.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.AppliContext;

/**
 * Created by yihong.ou on 17-9-27.
 */
public class LocalSearchAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<String> data, copyData;
    private Filter filter;

    public LocalSearchAdapter(Context context, List<String> data) {
        super();
        this.context = context;
        this.data = data;
        copyData = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("CutPasteId")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if (view == null) {
            viewHolder=new ViewHolder();
            view = View.inflate(AppliContext.sContext, R.layout.local_search_music_item, null);
            viewHolder.name=view.findViewById(R.id.tv_music_list_title);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.name.setText(data.get(i));
        return view;
    }
    private static class ViewHolder {
        public TextView name;
    }
    @Override
    public Filter getFilter() {

        if (filter == null) {
            filter = new MyFilter();
        }
        return filter;
    }
    class MyFilter extends Filter {
        /*调用一个工作线程过滤数据。子类必须实现该方法来执行过滤操作。过滤结果以Filter.FilterResults的形式返回*/
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String>filterData=new ArrayList<String>();
            if(constraint!=null&&constraint.toString().trim().length()>0){//输入的内容不为空
                String key=constraint.toString().trim().toLowerCase();//把输入的转为大写
                for (String i:copyData) {//遍历数据
                    if(i.toLowerCase().indexOf(key)!=-1){//取出的数据包含输入的那个字母
                        filterData.add(i);
                    }
                }
            }else{
                filterData=copyData;
            }
            FilterResults filterResults=new FilterResults();//过滤集
            filterResults.values=filterData;
            filterResults.count=filterData.size();
            return filterResults;
        }
        /*通过调用UI线程在用户界面发布过滤结果。来显示performFiltering(CharSequence)的过滤结果。*/
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            data=(List<String>) results.values;
            if(data!=null){//重绘当前可见区域
                notifyDataSetChanged();
            }else{//重绘控件（还原到初始状态）
                notifyDataSetInvalidated();
            }
        }

    }
}
