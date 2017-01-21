package ru.karamyshev.time.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.realm.RealmResults;
import ru.karamyshev.time.R;
import ru.karamyshev.time.database.model.RealmMemoir;
import ru.karamyshev.time.model.Memoir;
import ru.karamyshev.time.model.TimeType;
import ru.karamyshev.time.ui.adapters.MemoirAdapter;

public class MemoirsFragment extends BaseFragment {
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

        updateMemoirs();
        return view;
    }

    public void updateMemoirs() {
        RealmResults<RealmMemoir> memoirs = database.getMemoirs(timeType);
        updateAdapter(memoirs);
    }

    private void updateAdapter(List<? extends Memoir> memoirs) {
        if (memoirAdapter != null) {
            memoirAdapter.setMemoirList(memoirs);
            memoirAdapter.notifyDataSetChanged();
        }
    }
}
