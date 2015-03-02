package com.subaru.flexiblemiopon.view;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.subaru.flexiblemiopon.R;

import com.google.android.gms.plus.PlusOneButton;
import com.subaru.flexiblemiopon.data.PacketLogInfo;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com";

    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;

    private PlusOneButton mPlusOneButton;

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


        return view;
    }

    public void setPacketLog(PacketLogInfo packetLogInfo) {
        List<PacketLogInfo.HdoInfo.PacketLog> packetLogList = packetLogInfo.getHdoInfoList().get(0).getPacketLogList();

        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.RED);
        XYSeriesRenderer r2 = new XYSeriesRenderer();
        r2.setColor(Color.BLUE);
        r2.setLineWidth(3);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.addSeriesRenderer(r);
        renderer.addSeriesRenderer(r2);
        renderer.setMarginsColor(Color.WHITE);
        renderer.setPanEnabled(false, false);
        renderer.setXLabels(30);
        renderer.setYLabels(30);
        renderer.setLegendTextSize(30);
        renderer.setLabelsTextSize(20);
        renderer.setShowGrid(true);
        renderer.setXLabelsAlign(Paint.Align.RIGHT);
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
//        renderer.setZoomButtonsVisible(true);

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(getWithCouponLine(packetLogList));
        dataset.addSeries(getWithoutCouponLine(packetLogList));

        RelativeLayout layout = (RelativeLayout) getActivity().findViewById(R.id.layout);
        GraphicalView graph = ChartFactory.getLineChartView(getActivity(), dataset, renderer);

        layout.addView(graph);

    }

    private XYSeries getWithCouponLine(List<PacketLogInfo.HdoInfo.PacketLog> packetLogList) {
        XYSeries series = new XYSeries("With coupon");

        int i = 0;
        for (PacketLogInfo.HdoInfo.PacketLog info : packetLogList) {
            series.add(i, Integer.parseInt(info.getWithCoupon()));
        }
        return series;
    }

    private XYSeries getWithoutCouponLine(List<PacketLogInfo.HdoInfo.PacketLog> packetLogList) {
        XYSeries series = new XYSeries("Without coupon");

        int i = 0;
        for (PacketLogInfo.HdoInfo.PacketLog info : packetLogList) {
            series.add(i, Integer.parseInt(info.getWithoutCoupon()));
        }
        return series;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
