package com.example.firefighters.ui.fragments

import android.app.Dialog
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firefighters.R
import com.example.firefighters.adapters.EmergencyAdapter
import com.example.firefighters.databinding.FragmentEmergencyBinding
import com.example.firefighters.models.EmergencyModel
import com.example.firefighters.utils.ConstantsValues
import com.example.firefighters.utils.ConstantsValues.isIsChief
import com.example.firefighters.utils.ConstantsValues.isIsFirefighter
import com.example.firefighters.utils.ConstantsValues.unit
import com.example.firefighters.utils.InjectorUtils
import com.example.firefighters.viewmodels.models.EmergencyViewModel
import com.example.firefighters.viewmodels.models.MessageViewModel
import com.example.firefighters.viewmodels.models.UserViewModel
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import java.io.IOException

class EmergencyFragment : Fragment() {

    private lateinit var mDataBinding: FragmentEmergencyBinding

    //View models
    private val mEmergencyViewModel by viewModels<EmergencyViewModel> {
        InjectorUtils.provideEmergencyViewModel()
    }
    private val mMessageViewModel by viewModels<MessageViewModel> {
        InjectorUtils.provideMessageViewModel()
    }
    private val mUserViewModel by viewModels<UserViewModel> {
        InjectorUtils.provideUserViewModel()
    }

    private var emergencyAdapter: EmergencyAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private val loadQte = 10
    private var emergencies: ArrayList<EmergencyModel> = arrayListOf()
    private var lastFilter = ConstantsValues.FILTER_NAME
    private var lastOrder = Query.Direction.DESCENDING
    private var lastDocument: DocumentSnapshot? = null
    private val limitCount = 10
    private var mediaPlayer: MediaPlayer? = MediaPlayer()
    private val handlePlayingVoice = Handler(Looper.getMainLooper())
    private var isPlaying = false
    private var canRunThread = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        //Set content with data biding util
        mDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_emergency, container, false)
        val view = mDataBinding.root

        //Get last settings values
        lastFilter = ConstantsValues.FILTER_NAME
        lastOrder = Query.Direction.DESCENDING
        lastDocument = null
        emergencies = ArrayList()
        lastDocument = null

        initRecyclerView()
        checkInteractions()
        loadMoreEmergencies()

        return view
    }

    private fun checkInteractions() {
        mDataBinding.buttonImageFilter.setOnClickListener { showBottomSheetDialogFilter() }
        mDataBinding.buttonImageOrder.setOnClickListener { showBottomSheetDialogOrder() }
        mDataBinding.recyclerEmergencies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.canScrollVertically(1)) {
                    loadMoreEmergencies()
                }
            }
        })
    }

    private fun loadMoreEmergencies() {
        showLoadingEmergenciesView()
        mEmergencyViewModel.getEmergenciesQuery(lastDocument, lastFilter, lastOrder, limitCount)
            .observe(requireActivity()
            ) { queryDocumentSnapshots ->
                for (document: DocumentSnapshot in queryDocumentSnapshots) {
                    document.toObject(EmergencyModel::class.java)?.let { emergencies.add(it) }
                }
                emergencyAdapter?.notifyItemRangeInserted(
                    emergencyAdapter?.itemCount ?: 0,
                    emergencies.size
                )
                hideLoadingEmergencies()
                if (queryDocumentSnapshots.size() > 0 && queryDocumentSnapshots.documents.size > 0) lastDocument =
                    queryDocumentSnapshots.documents[queryDocumentSnapshots.size() - 1]
            }
    }

    private fun showLoadingEmergenciesView() {
        mDataBinding.progressLoadingEmergencies.show()
        mDataBinding.progressLoadingEmergencies.visibility = View.VISIBLE
        mDataBinding.textEmergencyTopTitle.visibility = View.INVISIBLE
    }

    private fun hideLoadingEmergencies() {
        mDataBinding.progressLoadingEmergencies.hide()
        mDataBinding.progressLoadingEmergencies.visibility = View.GONE
        mDataBinding.textEmergencyTopTitle.visibility = View.VISIBLE
    }

    private fun showDetailsDialog(position: Int) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_emergency_detail)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(AppCompatResources.getDrawable(requireContext(), R.color.transparent))
        dialog.findViewById<View>(R.id.button_close_dialog).setOnClickListener { dialog.dismiss() }
        val id = dialog.findViewById<MaterialTextView>(R.id.text_emergency_id)
        val status = dialog.findViewById<MaterialTextView>(R.id.text_emergency_status)
        val gravity = dialog.findViewById<MaterialTextView>(R.id.text_emergency_gravity)
        val assignedUnit = dialog.findViewById<MaterialTextView>(R.id.text_emergency_unit)
        val latitude = dialog.findViewById<MaterialTextView>(R.id.text_emergency_latitude)
        val longitude = dialog.findViewById<MaterialTextView>(R.id.text_emergency_longitude)
        val sender = dialog.findViewById<MaterialTextView>(R.id.text_emergency_sender_mail)
        var tempAssignedUnit: String? = "<< None >>"
        var tempSender: String? = "<< Unknown >>"
        val tempId = "EM" + emergencies[position].id
        val tempStatus = emergencies[position].status
        val tempGravity = emergencies[position].gravity
        val tempLatitude = emergencies[position].latitude.toString() + ""
        val tempLongitude = emergencies[position].longitude.toString() + ""
        if (emergencies[position].currentUnit != null && emergencies[position].currentUnit?.isNotEmpty() == false) tempAssignedUnit =
            emergencies[position].currentUnit
        if (emergencies[position].senderMail != null && emergencies[position].senderMail?.isEmpty() == false) tempSender =
            emergencies[position].senderMail
        id.text = tempId
        status.text = tempStatus
        gravity.text = tempGravity
        assignedUnit.text = tempAssignedUnit
        latitude.text = tempLatitude
        longitude.text = tempLongitude
        sender.text = tempSender
        if (emergencies[position].messageId > 0) {
            val relativeLayoutPlayView =
                dialog.findViewById<RelativeLayout>(R.id.relative_play_audio_view)
            val relativeLayoutNoAudioView =
                dialog.findViewById<RelativeLayout>(R.id.relative_no_audio_view)
            val seekBar = dialog.findViewById<SeekBar>(R.id.seek_bar_audio_progress)
            val buttonPlayStopAudio =
                dialog.findViewById<ImageView>(R.id.button_emergency_play_pause_audio)
            val imagePlace = dialog.findViewById<SimpleDraweeView>(R.id.image_emergency_message)
            val message = dialog.findViewById<MaterialTextView>(R.id.text_emergency_message)
            mMessageViewModel.loadMessageModel(emergencies.get(position).messageId)
                .observe(requireActivity()
                ) { messageModel ->
                    if (messageModel != null) {
                        message.text = messageModel.message
                        imagePlace.setImageURI(messageModel.imagesSrc)
                        if (messageModel.audioSrc != null && messageModel.audioSrc?.isEmpty() != true) {
                            showEmergencyAudio(
                                relativeLayoutPlayView,
                                relativeLayoutNoAudioView
                            )
                            prepareEmergencyAudio(
                                messageModel.audioSrc,
                                buttonPlayStopAudio,
                                seekBar,
                                buttonPlayStopAudio
                            )
                        } else {
                            hideEmergencyAudio(
                                relativeLayoutPlayView,
                                relativeLayoutNoAudioView
                            )
                        }
                    }
                }
        }
        dialog.show()
    }

    private fun hideEmergencyAudio(
        relativeLayoutPlayView: RelativeLayout,
        relativeLayoutNoAudioView: RelativeLayout
    ) {
        relativeLayoutPlayView.visibility = View.GONE
        relativeLayoutNoAudioView.visibility = View.VISIBLE
    }

    private fun showEmergencyAudio(
        relativeLayoutPlayView: RelativeLayout,
        relativeLayoutNoAudioView: RelativeLayout
    ) {
        relativeLayoutPlayView.visibility = View.VISIBLE
        relativeLayoutNoAudioView.visibility = View.GONE
    }

    private fun prepareEmergencyAudio(
        audioSrc: String?,
        buttonPlayAudio: ImageView,
        seekBar: SeekBar,
        buttonPlayStopAudio: ImageView
    ) {
        buttonPlayAudio.setOnClickListener {
            if (!isPlaying) {
                playAudio(audioSrc, seekBar, buttonPlayStopAudio)
            } else {
                stopPlay()
                setPlayingView(false, buttonPlayStopAudio)
            }
        }
    }

    private fun playAudio(audioSrc: String?, seekBar: SeekBar, buttonPlayStopAudio: ImageView) {
        resetProgress(seekBar)
        stopPlay()
        setPlayingView(true, buttonPlayStopAudio)
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        try {
            mediaPlayer?.setDataSource(audioSrc)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        isPlaying = true
        canRunThread = true
        Thread {
            if (canRunThread) {
                mediaPlayer?.prepareAsync()
                mediaPlayer?.setOnPreparedListener {
                    Toast.makeText(
                        context,
                        ((mediaPlayer?.duration ?: 0) * 1000).toString() + "",
                        Toast.LENGTH_SHORT
                    ).show()
                    mediaPlayer?.start()
                    val duration = mediaPlayer?.duration?.toLong()
                    if (duration != null) {
                        seekBar.max = (duration / 100).toInt()
                    }
                    mediaPlayer?.setOnCompletionListener {
                        setPlayingView(false, buttonPlayStopAudio)
                        resetProgress(seekBar)
                        stopPlay()
                    }
                    val runnablePlayVoice: Runnable = object : Runnable {
                        override fun run() {
                            if (mediaPlayer != null) {
                                val temMediaPlayerProgress = mediaPlayer?.currentPosition
                                val progress = temMediaPlayerProgress?.div(100)
                                if (progress != null) {
                                    setProgress(seekBar, progress)
                                }
                                handlePlayingVoice.postDelayed(this, 100)
                            } else {
                                resetProgress(seekBar)
                            }
                        }
                    }
                    //Start
                    handlePlayingVoice.postDelayed(runnablePlayVoice, 100)
                }
            }
        }.start()
    }

    private fun stopPlay() {
        canRunThread = false
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
        isPlaying = false
        mediaPlayer = null
    }

    private fun setPlayingView(play: Boolean, buttonPlayStopAudio: ImageView) {
        if (play) {
            buttonPlayStopAudio.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_round_stop_24))
        } else {
            buttonPlayStopAudio.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_round_play_arrow_24))
        }
    }

    private fun setProgress(seekBar: SeekBar, progress: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            seekBar.setProgress(progress, true)
        } else {
            seekBar.progress = progress
        }
    }

    private fun resetProgress(seekBar: SeekBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            seekBar.setProgress(0, true)
        } else {
            seekBar.progress = 0
        }
    }

    private fun showStreetMap(position: Int) {
        //
    }

    private fun showBottomSheetDialogEmergencyMore(position: Int) {
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(R.layout.bottom_sheet_layout_emergency_more)
        bottomSheet.setCancelable(true)
        bottomSheet.setCanceledOnTouchOutside(true)
        bottomSheet.dismissWithAnimation = true
        bottomSheet.window?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)

        //Check if is working or not and if it's firefighter
        if (isIsFirefighter && isIsChief) {
            if ((emergencies[position].status == ConstantsValues.WORKING)) {
                bottomSheet.findViewById<View>(R.id.linear_work_emergency)?.visibility = View.GONE
                bottomSheet.findViewById<View>(R.id.linear_finish_work_emergency)?.visibility =
                    View.VISIBLE
            } else if (emergencies[position].status != ConstantsValues.WORKING && emergencies[position].status != ConstantsValues.FINISHED
            ) {
                bottomSheet.findViewById<View>(R.id.linear_work_emergency)?.visibility =
                    View.VISIBLE
                bottomSheet.findViewById<View>(R.id.linear_finish_work_emergency)?.visibility =
                    View.GONE
            } else {
                bottomSheet.findViewById<View>(R.id.linear_work_emergency)?.visibility = View.GONE
                bottomSheet.findViewById<View>(R.id.linear_finish_work_emergency)?.visibility =
                    View.GONE
            }
        } else {
            bottomSheet.findViewById<View>(R.id.linear_work_emergency)?.visibility = View.GONE
            bottomSheet.findViewById<View>(R.id.linear_finish_work_emergency)?.visibility =
                View.GONE
        }
        bottomSheet.findViewById<View>(R.id.button_close_dialog)?.setOnClickListener(
            View.OnClickListener { bottomSheet.dismiss() })
        bottomSheet.findViewById<View>(R.id.button_work_on)
            ?.setOnClickListener {
                setEmergencyStatus(
                    bottomSheet,
                    position,
                    ConstantsValues.WORKING
                )
            }
        bottomSheet.findViewById<View>(R.id.button_finish_emergency)
            ?.setOnClickListener {
                setEmergencyStatus(
                    bottomSheet,
                    position,
                    ConstantsValues.FINISHED
                )
            }
        bottomSheet.findViewById<View>(R.id.button_details_emergency)
            ?.setOnClickListener {
                bottomSheet.dismiss()
                showDetailsDialog(position)
            }
        bottomSheet.show()
    }

    private fun setEmergencyStatus(bottomSheet: BottomSheetDialog, position: Int, status: String) {
        bottomSheet.dismiss()
        val em = emergencies[position]
        em.status = status
        val tempUnit = unit
        em.currentUnit = tempUnit
        if (!tempUnit.isNullOrEmpty()) {
            mEmergencyViewModel.getEmergencyWorkingOnModel(tempUnit)
                .observe(requireActivity()
                ) { value ->
                    if (value != null) {
                        Toast.makeText(
                            context,
                            "You cannot work on 2 places !",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        mEmergencyViewModel.updateEmergency(em)
                            .observe(requireActivity()
                            ) {
                                if ((it ?: 0) >= 1) {
                                    emergencies[position] = em
                                    emergencyAdapter?.notifyItemChanged(position)
                                    Toast.makeText(context, "Updated !", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Error  update !",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                }
        } else {
            Toast.makeText(context, "You are not allowed !", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showBottomSheetDialogFilter() {
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(R.layout.bottom_sheet_layout_emergency_filter)
        bottomSheet.setCancelable(true)
        bottomSheet.setCanceledOnTouchOutside(true)
        bottomSheet.window?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)
        bottomSheet.findViewById<View>(R.id.button_close_dialog)
            ?.setOnClickListener { bottomSheet.dismiss() }
        bottomSheet.findViewById<View>(R.id.button_name_filter)
            ?.setOnClickListener {
                filterResultsBy(ConstantsValues.FILTER_NAME)
                bottomSheet.dismiss()
            }
        bottomSheet.findViewById<View>(R.id.button_degree_filter)
            ?.setOnClickListener(View.OnClickListener {
                filterResultsBy(ConstantsValues.FILTER_DEGREE)
                bottomSheet.dismiss()
            })
        bottomSheet.findViewById<View>(R.id.button_status_filter)
            ?.setOnClickListener(View.OnClickListener {
                filterResultsBy(ConstantsValues.FILTER_STATUS)
                bottomSheet.dismiss()
            })
        bottomSheet.show()
    }

    private fun filterResultsBy(filterName: String) {
        lastFilter = filterName
        emergencies.clear()
        emergencyAdapter?.notifyDataSetChanged()
        lastDocument = null
        loadMoreEmergencies()
    }

    private fun showBottomSheetDialogOrder() {
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(R.layout.bottom_sheet_layout_emergency_order)
        bottomSheet.setCancelable(true)
        bottomSheet.setCanceledOnTouchOutside(true)
        bottomSheet.window?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)
        bottomSheet.findViewById<View>(R.id.button_close_dialog)
            ?.setOnClickListener { bottomSheet.dismiss() }
        bottomSheet.findViewById<View>(R.id.button_asc_order)
            ?.setOnClickListener {
                reorderResultsBy(Query.Direction.ASCENDING)
                bottomSheet.dismiss()
            }
        bottomSheet.findViewById<View>(R.id.button_desc_order)
            ?.setOnClickListener(View.OnClickListener {
                reorderResultsBy(Query.Direction.DESCENDING)
                bottomSheet.dismiss()
            })
        bottomSheet.show()
    }

    private fun reorderResultsBy(order: Query.Direction) {
        lastOrder = order
        emergencies.clear()
        lastDocument = null
        loadMoreEmergencies()
    }

    private fun initRecyclerView() {
        emergencies = ArrayList()
        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        emergencyAdapter = EmergencyAdapter(
            requireContext(),
            emergencies,
            object : EmergencyAdapter.OnItemClickListener {
                override fun onItemMoreClick(position: Int) {
                    showBottomSheetDialogEmergencyMore(position)
                }

                override fun onItemStreetClick(position: Int) {
                    showStreetMap(position)
                }

                override fun onItemCardClick(position: Int) {
                    showDetailsDialog(position)
                }
            })
        mDataBinding.recyclerEmergencies.layoutManager = layoutManager
        mDataBinding.recyclerEmergencies.adapter = emergencyAdapter
    }

    override fun onStart() {
        super.onStart()
        canRunThread = false
    }

    override fun onResume() {
        super.onResume()
        canRunThread = false
    }

    override fun onPause() {
        super.onPause()
        canRunThread = false
    }

    override fun onStop() {
        super.onStop()
        canRunThread = false
    }

    override fun onLowMemory() {
        super.onLowMemory()
        canRunThread = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        canRunThread = false
    }

    override fun onDestroy() {
        super.onDestroy()
        canRunThread = false
    }

    override fun onDetach() {
        super.onDetach()
        canRunThread = false
    }
}