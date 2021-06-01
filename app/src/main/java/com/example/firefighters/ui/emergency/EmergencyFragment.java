package com.example.firefighters.ui.emergency;

import android.app.Dialog;
import android.content.Context;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firefighters.R;
import com.example.firefighters.adapters.EmergencyAdapter;
import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.viewmodels.EmergencyViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EmergencyFragment extends Fragment {

    private EmergencyViewModel emergencyViewModel;
    private EmergencyAdapter emergencyAdapter;
    private RecyclerView emergenciesRecyclerView;

    private Context context;

    //Image views
    private ImageView buttonFilter;
    private ImageView buttonOrder;

    //Text views
    private TextView textEmergencyTitle;

    //Progress indicator
    CircularProgressIndicator circularProgressIndicator;
    private LinearLayoutManager layoutManager;
    private int loadQte = 10;

    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);
        context = view.getContext();
        initViews(view);
        initViewModel();
        checkInteractions();
        initRecyclerView(view);
        observeLiveData();
        loadFirstData(loadQte);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void checkInteractions() {
        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogFilter(v);
            }
        });
        buttonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogOrder(v);
            }
        });
        emergenciesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { //check for scroll down
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            emergencyViewModel.loadEmergencies(requireActivity(), loadQte);
                            loading = true;
                        }
                    }
                }
//                if (layoutManager.findLastVisibleItemPosition() >= emergencyViewModel.getEmergencies().getValue().size() - 1 && !emergencyViewModel.getIsLoading().getValue()){
//                    emergencyViewModel.loadEmergencies(requireActivity(), loadQte);
//                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        emergencyViewModel.clearEmergencies();
    }

    private void observeLiveData() {
        emergencyViewModel.getEmergencies().observe(getViewLifecycleOwner(), new Observer<ArrayList<EmergencyModel>>() {
            @Override
            public void onChanged(ArrayList<EmergencyModel> emergencyModels) {
                //
            }
        });
        emergencyViewModel.getIsLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showLoadingView();
                }else{
                    hideLoadingView();
                    if(emergencyViewModel.getEmergencies().getValue() != null && emergencyViewModel.getEmergencies().getValue().size() > 0){
                        emergencyAdapter.notifyItemRangeInserted(emergencyViewModel.getEmergencies().getValue().size(), loadQte);
                    }
                }
            }
        });
    }

    private void hideLoadingView() {
        new CountDownTimer(500, 1000){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                textEmergencyTitle.setVisibility(View.VISIBLE);
                circularProgressIndicator.hide();
            }
        }.start();
    }

    private void showLoadingView() {
        textEmergencyTitle.setVisibility(View.INVISIBLE);
        circularProgressIndicator.show();
    }

    private void initRecyclerView(View view) {
        layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        emergencyAdapter = new EmergencyAdapter(context, emergencyViewModel.getEmergencies().getValue(), new EmergencyAdapter.OnItemClickListener() {
            @Override
            public void onItemMoreClick(int position) {
                showBottomSheetDialogEmergencyMore(view, position);
            }

            @Override
            public void onItemStreetClick(int position) {
                showStreetMap(view, position);
            }

            @Override
            public void onItemCardClick(int position) {
                showDetailsDialog(view, position);
            }
        });
        emergenciesRecyclerView.setLayoutManager(layoutManager);
        emergenciesRecyclerView.setAdapter(emergencyAdapter);
    }

    private void showDetailsDialog(View view, int position) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_emergency_detail);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        dialog.findViewById(R.id.button_close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showStreetMap(View view, int position) {
        //
    }

    private void showBottomSheetDialogEmergencyMore(View view, int position) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        bottomSheet.setContentView(R.layout.bottom_sheet_layout_emergency_more);
        bottomSheet.setCancelable(true);
        bottomSheet.setCanceledOnTouchOutside(true);
        bottomSheet.setDismissWithAnimation(true);
        bottomSheet.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        bottomSheet.findViewById(R.id.button_close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
            }
        });
        //Check if is working or not and if it's firefighter or admin
        if(1 <= 2){
            bottomSheet.findViewById(R.id.linear_work_on_emergency).setVisibility(View.VISIBLE);
            bottomSheet.findViewById(R.id.linear_pause_finish_emergency).setVisibility(View.GONE);
        }else{
            bottomSheet.findViewById(R.id.linear_pause_finish_emergency).setVisibility(View.VISIBLE);
            bottomSheet.findViewById(R.id.linear_work_on_emergency).setVisibility(View.GONE);
        }

        bottomSheet.findViewById(R.id.button_work_on).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEmergencyWorkOn(bottomSheet, position);
            }
        });
        bottomSheet.findViewById(R.id.button_finish_emergency);
        bottomSheet.findViewById(R.id.button_pause_emergency);

        bottomSheet.findViewById(R.id.button_details_emergency);
        bottomSheet.findViewById(R.id.button_signal_emergency);
        bottomSheet.show();
    }

    private void setEmergencyWorkOn(BottomSheetDialog bottomSheet ,int position) {
        bottomSheet.dismiss();
        Toast.makeText(context, emergencyViewModel.getEmergencies().getValue().get(position)+"", Toast.LENGTH_SHORT).show();
    }

    private void showBottomSheetDialogFilter(View view) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        bottomSheet.setContentView(R.layout.bottom_sheet_layout_emergency_filter);
        bottomSheet.setCancelable(true);
        bottomSheet.setCanceledOnTouchOutside(true);
        bottomSheet.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        bottomSheet.findViewById(R.id.button_close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
            }
        });
        bottomSheet.findViewById(R.id.button_name_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterResultsBy(ConstantsValues.FILTER_NAME);
                bottomSheet.dismiss();
            }
        });
        bottomSheet.findViewById(R.id.button_degree_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterResultsBy(ConstantsValues.FILTER_DEGREE);
                bottomSheet.dismiss();
            }
        });
        bottomSheet.findViewById(R.id.button_status_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterResultsBy(ConstantsValues.FILTER_STATUS);
                bottomSheet.dismiss();
            }
        });
        bottomSheet.show();
    }

    private void filterResultsBy(String filterName) {
        emergencyViewModel.setFilterEmergencies(filterName);
        emergencyViewModel.firstLoad(requireActivity(), loadQte);
        Toast.makeText(context, filterName+"", Toast.LENGTH_SHORT).show();
    }

    private void showBottomSheetDialogOrder(View view) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        bottomSheet.setContentView(R.layout.bottom_sheet_layout_emergency_order);
        bottomSheet.setCancelable(true);
        bottomSheet.setCanceledOnTouchOutside(true);
        bottomSheet.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        bottomSheet.findViewById(R.id.button_close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
            }
        });
        bottomSheet.findViewById(R.id.button_asc_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reorderResultsBy(Query.Direction.ASCENDING);
                bottomSheet.dismiss();
            }
        });
        bottomSheet.findViewById(R.id.button_desc_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reorderResultsBy(Query.Direction.DESCENDING);
                bottomSheet.dismiss();
            }
        });
        bottomSheet.show();
    }

    private void reorderResultsBy(Query.Direction order) {
        emergencyViewModel.setOrderEmergencies(order);
        emergencyViewModel.firstLoad(requireActivity(), loadQte);
        Toast.makeText(context, order+"", Toast.LENGTH_SHORT).show();
    }

    private void loadFirstData(int qte) {
        emergencyViewModel.clearEmergencies();
        emergencyViewModel.firstLoad(requireActivity(), qte);
    }

    private void initViewModel() {
        emergencyViewModel = new ViewModelProvider(requireActivity()).get(EmergencyViewModel.class);
        emergencyViewModel.init();
    }

    private void initViews(View view){
        emergenciesRecyclerView = view.findViewById(R.id.recycler_emergencies);

        //Buttons
        buttonFilter = view.findViewById(R.id.button_image_filter);
        buttonOrder = view.findViewById(R.id.button_image_order);

        //Text view
        textEmergencyTitle = (TextView) view.findViewById(R.id.text_emergency_top_title);

        //Circular progress indicator
        circularProgressIndicator = (CircularProgressIndicator) view.findViewById(R.id.progress_emergency);
    }
}