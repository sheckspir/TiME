package ru.karamyshev.time.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.karamyshev.time.R;
import ru.karamyshev.time.database.DatabaseResults;
import ru.karamyshev.time.database.model.RealmMemoir;
import ru.karamyshev.time.model.TimeType;
import ru.karamyshev.time.ui.adapters.MemoirAdapter;

public class MemoirsFragment extends BaseFragment implements DatabaseResults.OnChangeListener {
    private static final String ARG_MEMOIR_TYPE = "time_type";

    public static MemoirsFragment newInstance(TimeType timeType) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEMOIR_TYPE, timeType);
        MemoirsFragment fragment = new MemoirsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private TimeType timeType = TimeType.DAY;
    private MemoirAdapter memoirAdapter;
    private DatabaseResults<RealmMemoir> memoirs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_MEMOIR_TYPE)) {
            timeType = (TimeType) getArguments().getSerializable(ARG_MEMOIR_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memoirs, container, false);
        RecyclerView memoirsRecycler = (RecyclerView) view.findViewById(R.id.memoirs_recycler);
        memoirsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        memoirAdapter = new MemoirAdapter();
        memoirsRecycler.setAdapter(memoirAdapter);
        memoirs = database.getMemoirs(timeType);
        memoirs.setOnChangeListener(this);
        memoirAdapter.setMemoirList(memoirs);
        memoirAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onDestroyView() {
        if (memoirs != null) {
            memoirs.close();
        }
        super.onDestroyView();
    }

    @Override
    public void onChange() {
        if (memoirAdapter != null) {
            memoirAdapter.notifyDataSetChanged();
        }
    }
}
