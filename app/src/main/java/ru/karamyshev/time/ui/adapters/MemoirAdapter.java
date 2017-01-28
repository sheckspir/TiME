package ru.karamyshev.time.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import ru.karamyshev.time.R;
import ru.karamyshev.time.model.Memoir;

public class MemoirAdapter extends RecyclerView.Adapter<MemoirAdapter.MemoirsViewHolder> {
    private static final SimpleDateFormat dayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat weekDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat monthDateFormat = new SimpleDateFormat("MMM yyyy");
    private static final SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");

    class MemoirsViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        TextView textText;

        MemoirsViewHolder(View itemView) {
            super(itemView);
            dateText = (TextView) itemView.findViewById(R.id.memoir_date_text);
            textText = (TextView) itemView.findViewById(R.id.memoir_text_text);
        }

        void bind(Memoir memoir) {
            textText.setText(memoir.getText());
            switch (memoir.getTimeType()) {
                case DAY:
                    dateText.setText(dayDateFormat.format(memoir.getDate()));
                    break;
                case WEEK:
                    dateText.setText(weekDateFormat.format(memoir.getDate()));
                    break;
                case MONTH:
                    dateText.setText(monthDateFormat.format(memoir.getDate()));
                    break;
                case YEAR:
                    dateText.setText(yearDateFormat.format(memoir.getDate()));
                    break;
            }
        }
    }

    private List<? extends Memoir> memoirList;

    public MemoirAdapter() {
    }

    public void setMemoirList(List<? extends Memoir> memoirList) {
        this.memoirList = memoirList;
    }

    @Override
    public MemoirsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memoir, parent, false);
        return new MemoirsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MemoirsViewHolder holder, int position) {
        holder.bind(memoirList.get(position));
    }

    @Override
    public int getItemCount() {
        return memoirList.size();
    }
}
