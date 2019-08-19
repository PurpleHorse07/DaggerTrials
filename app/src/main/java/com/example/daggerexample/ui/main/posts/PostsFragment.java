package com.example.daggerexample.ui.main.posts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daggerexample.R;
import com.example.daggerexample.models.Post;
import com.example.daggerexample.ui.main.Resource;
import com.example.daggerexample.ui.main.profile.ProfileViewModel;
import com.example.daggerexample.utils.VSpaceItemDecor;
import com.example.daggerexample.viewmodels.ViewModelProviderFactory;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class PostsFragment extends DaggerFragment {

    private static final String TAG = "PostsFragment";
    private RecyclerView recyclerView;
    private PostsViewModel postsViewModel;
    private ProgressBar progressBar;
    private TextView noData;

    @Inject
    PostsRecyclerAdapter adapter;

    @Inject
    ViewModelProviderFactory providerFactory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Toast.makeText(getActivity(), "Post Fragment", Toast.LENGTH_SHORT).show();
        return inflater.inflate(R.layout.fragment_posts,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: POSTS CREATED");
        recyclerView=view.findViewById(R.id.list);
        progressBar=view.findViewById(R.id.progress);
        noData=view.findViewById(R.id.nodata);
        postsViewModel=new ViewModelProvider(this,providerFactory).get(PostsViewModel.class);

        initRecyclerView();
        subscribeObservers();
    }

    private void subscribeObservers(){
        postsViewModel.observePosts().removeObservers(getViewLifecycleOwner());
        postsViewModel.observePosts().observe(getViewLifecycleOwner(), new Observer<Resource<List<Post>>>() {
            @Override
            public void onChanged(Resource<List<Post>> listResource) {
                if(listResource!=null){
                    Log.d(TAG, "onChanged: "+listResource.data);
                    switch (listResource.status){
                        case LOADING:{
                            showProgressBar(true);
                            showText(false);
                            Log.d(TAG, "onChanged: LOADING DATA...");
                            break;
                        }
                        case ERROR:{
                            showProgressBar(false);
                            showText(true);
                            Log.d(TAG, "onChanged: ERROR: "+listResource.message );
                            break;
                        }
                        case SUCCESS:{
                            showProgressBar(false);
                            showText(false);
                            Log.d(TAG, "onChanged: SUCCESS DATA...");
                            adapter.setPosts(listResource.data);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void initRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new VSpaceItemDecor(15));
        recyclerView.setAdapter(adapter);
    }

    private void showProgressBar(boolean isVisible){
        if(isVisible)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    private void showText(boolean isVisible){
        if(isVisible)
            noData.setVisibility(View.VISIBLE);
        else
            noData.setVisibility(View.GONE);
    }
}
