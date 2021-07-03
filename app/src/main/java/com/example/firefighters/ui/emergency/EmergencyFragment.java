package com.example.firefighters.ui.emergency;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firefighters.R;
import com.example.firefighters.adapters.EmergencyAdapter;
import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.UserModel;
import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.viewmodels.EmergencyViewModel;
import com.example.firefighters.viewmodels.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EmergencyFragment extends Fragment {

    private EmergencyViewModel emergencyViewModel;
    private EmergencyAdapter emergencyAdapter;
    private RecyclerView emergenciesRecyclerView;
    private Context context;
    //Image views
    private ImageView buttonFilter;
    private ImageView buttonOrder;
    //Text views
    private LinearLayoutManager layoutManager;
    private final int loadQte = 10;
    private ArrayList<EmergencyModel> emergencies;

    private String lastFilter = ConstantsValues.FILTER_STATUS;
    private Query.Direction lastOrder = Query.Direction.DESCENDING;
    DocumentSnapshot lastDocument;
    private int limitCount = 10;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);
        context = view.getContext();
        lastFilter = ConstantsValues.FILTER_STATUS;
        lastOrder = Query.Direction.DESCENDING;
        lastDocument = null;
        emergencies = new ArrayList<>();
        lastDocument = null;
        initViews(view);
        checkInteractions();
        initRecyclerView(view);
        loadMoreEmergencies();
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
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){
                    loadMoreEmergencies();
                }
            }
        });
    }

    private void loadMoreEmergencies() {
        emergencyViewModel.getEmergenciesQuery(lastDocument, lastFilter, lastOrder, limitCount).observe(requireActivity(), new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document:queryDocumentSnapshots) {
                    emergencies.add(document.toObject(EmergencyModel.class));
                }
                emergencyAdapter.notifyItemRangeInserted(emergencyAdapter.getItemCount(), emergencies.size());
                if (queryDocumentSnapshots.size() > 0 && queryDocumentSnapshots.getDocuments().size() > 0)
                    lastDocument = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                if (getActivity() != null)
                    emergencyViewModel.getEmergenciesQuery(lastDocument, lastFilter, lastOrder, limitCount).removeObservers(requireActivity());
            }
        });
    }

    private void showDetailsDialog(int position) {
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

        //Check if is working or not and if it's firefighter
        if(ConstantsValues.isIsFirefighter() && ConstantsValues.isIsChief()){
            if (emergencies.get(position).getStatus().equals(ConstantsValues.WORKING)) {
                bottomSheet.findViewById(R.id.linear_work_emergency).setVisibility(View.GONE);
                bottomSheet.findViewById(R.id.linear_finish_work_emergency).setVisibility(View.VISIBLE);
            } else if(!emergencies.get(position).getStatus().equals(ConstantsValues.WORKING) && !emergencies.get(position).getStatus().equals(ConstantsValues.FINISHED)) {
                bottomSheet.findViewById(R.id.linear_work_emergency).setVisibility(View.VISIBLE);
                bottomSheet.findViewById(R.id.linear_finish_work_emergency).setVisibility(View.GONE);
            }else{
                bottomSheet.findViewById(R.id.linear_work_emergency).setVisibility(View.GONE);
                bottomSheet.findViewById(R.id.linear_finish_work_emergency).setVisibility(View.GONE);
            }
        }else {
            bottomSheet.findViewById(R.id.linear_work_emergency).setVisibility(View.GONE);
            bottomSheet.findViewById(R.id.linear_finish_work_emergency).setVisibility(View.GONE);
        }
        bottomSheet.findViewById(R.id.button_close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
            }
        });
        bottomSheet.findViewById(R.id.button_work_on).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEmergencyWorkOn(bottomSheet, position);
            }
        });
        bottomSheet.findViewById(R.id.button_finish_emergency).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEmergencyFinished(bottomSheet, position);
            }
        });
        bottomSheet.findViewById(R.id.button_details_emergency).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
                showDetailsDialog(position);
            }
        });
        bottomSheet.show();
    }

    private void setEmergencyFinished(BottomSheetDialog bottomSheet, int position) {
        bottomSheet.dismiss();
        EmergencyModel em = emergencies.get(position);
        em.setStatus(ConstantsValues.FINISHED);
        emergencyViewModel.updateEmergency(em).observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer >= 1){
                    emergencies.set(position, em);
                    emergencyAdapter.notifyItemChanged(position);
                    Toast.makeText(context, "Updated !", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Error  update !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setEmergencyWorkOn(BottomSheetDialog bottomSheet, int position) {
        bottomSheet.dismiss();
        EmergencyModel em = emergencies.get(position);
        em.setStatus(ConstantsValues.WORKING);
        emergencyViewModel.updateEmergency(em).observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer >= 1){
                    emergencies.set(position, em);
                    emergencyAdapter.notifyItemChanged(position);
                    Toast.makeText(context, "Updated !", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Error  update !", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        lastFilter = filterName;
        emergencies.clear();
        lastDocument = null;
        loadMoreEmergencies();
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
        lastOrder = order;
        emergencies.clear();
        lastDocument = null;
        loadMoreEmergencies();
    }

    private void initRecyclerView(View view) {
        emergencies = new ArrayList<>();
        layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        emergencyAdapter = new EmergencyAdapter(context, emergencies, new EmergencyAdapter.OnItemClickListener() {
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
                showDetailsDialog(position);
            }
        });
        emergenciesRecyclerView.setLayoutManager(layoutManager);
        emergenciesRecyclerView.setAdapter(emergencyAdapter);
    }

    private void initViewModel() {
        emergencyViewModel = new ViewModelProvider(requireActivity()).get(EmergencyViewModel.class);
        emergencyViewModel.init();
    }

    private void initViews(View view) {
        emergenciesRecyclerView = view.findViewById(R.id.recycler_emergencies);

        //Buttons
        buttonFilter = view.findViewById(R.id.button_image_filter);
        buttonOrder = view.findViewById(R.id.button_image_order);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}