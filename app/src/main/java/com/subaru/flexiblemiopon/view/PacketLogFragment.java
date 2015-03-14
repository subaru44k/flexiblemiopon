package com.subaru.flexiblemiopon.view;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.subaru.flexiblemiopon.R;

import com.subaru.flexiblemiopon.data.PacketLogInfo;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.List;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link PacketLogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PacketLogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PacketLogFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String LOG_TAG = PacketLogFragment.class.getName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PacketLogInfo mPacketLogInfo;
    private Spinner mSpinner;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlusOneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PacketLogFragment newInstance(String param1, String param2) {
        PacketLogFragment fragment = new PacketLogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PacketLogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_packetlog, container, false);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.product_image_loading);
        progressBar.setVisibility(View.VISIBLE);

        mSpinner = (Spinner) view.findViewById(R.id.spinner_packet_graph_duration);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (mPacketLogInfo != null) {
                    refreshGraph();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    /**
     * Set packet log to the fragment
     * @param packetLogInfo
     */
    public void setPacketLog(PacketLogInfo packetLogInfo) {
        mPacketLogInfo = packetLogInfo;
    }

    /**
     * Refresh the graph based on packetLog
     */
    public void refreshGraph() {
        RelativeLayout layout = (RelativeLayout) getActivity().findViewById(R.id.layout);
        layout.removeAllViews();

        List<PacketLogInfo.HdoInfo.PacketLog> packetLogList = mPacketLogInfo.getHdoInfoList().get(0).getPacketLogList();

        GraphicalView graph = getPacketLogGraph(packetLogList);

        layout.addView(graph);
    }

    private GraphicalView getPacketLogGraph(List<PacketLogInfo.HdoInfo.PacketLog> packetLogList) {
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(getResources().getColor(R.color.bright_foreground_disabled_material_light));
        r.setLineWidth(5);
        r.setPointStyle(PointStyle.CIRCLE);
        XYSeriesRenderer r2 = new XYSeriesRenderer();
        r2.setColor(Color.BLUE);
        r2.setLineWidth(5);
        r2.setPointStyle(PointStyle.CIRCLE);

        String durationString = (String) mSpinner.getSelectedItem();
        XYMultipleSeriesRenderer renderer = getXyMultipleSeriesRenderer(r, r2, getDuration(durationString));

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(getWithCouponLine(packetLogList));
        dataset.addSeries(getWithoutCouponLine(packetLogList));

        LayoutInflater.from(getActivity()).inflate(R.layout.fragment_packetlog, null);
        return ChartFactory.getLineChartView(getActivity(), dataset, renderer);
    }

    private int getDuration(String durationString) {
        return Integer.parseInt(durationString.substring(0, durationString.indexOf(" ")));
    }

    private XYMultipleSeriesRenderer getXyMultipleSeriesRenderer(XYSeriesRenderer r, XYSeriesRenderer r2, int duration) {
        int xAxisMax = 30;

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.addSeriesRenderer(r);
        renderer.addSeriesRenderer(r2);

        renderer.setPanEnabled(false, false);
        renderer.setXLabels(0);
        renderer.setAxisTitleTextSize(30);

        for (int i=0; i<=duration; i += duration / 5) {
            renderer.addXTextLabel(xAxisMax - i, "-" + Integer.toString(i) + getString(R.string.graph_x_digit));
        }

        renderer.setYLabels(10);
        renderer.setYTitle("MB");
        renderer.setLegendTextSize(30);
        renderer.setLabelsTextSize(20);
        renderer.setXAxisMin(xAxisMax - duration);
        renderer.setXAxisMax(xAxisMax);
        renderer.setShowGrid(true);
        renderer.setXLabelsAlign(Paint.Align.RIGHT);
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setMargins(new int[]{50, 50, 50, 50});

        renderer.setApplyBackgroundColor(true);
        renderer.setMarginsColor(Color.parseColor("#2792c3"));
        renderer.setBackgroundColor(getResources().getColor(R.color.bright_foreground_disabled_material_dark));

        return renderer;
    }

    private XYSeries getWithCouponLine(List<PacketLogInfo.HdoInfo.PacketLog> packetLogList) {
        XYSeries series = new XYSeries(getString(R.string.legend_with_coupon));

        int i = 0;
        for (PacketLogInfo.HdoInfo.PacketLog info : packetLogList) {
            series.add(i, Integer.parseInt(info.getWithCoupon()));
            i++;
        }
        return series;
    }

    private XYSeries getWithoutCouponLine(List<PacketLogInfo.HdoInfo.PacketLog> packetLogList) {
        XYSeries series = new XYSeries(getString(R.string.legend_without_coupon));

        int i = 0;
        for (PacketLogInfo.HdoInfo.PacketLog info : packetLogList) {
            series.add(i, Integer.parseInt(info.getWithoutCoupon()));
            i++;
        }
        return series;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
