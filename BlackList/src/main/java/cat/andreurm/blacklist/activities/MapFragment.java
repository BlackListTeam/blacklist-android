package cat.andreurm.blacklist.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import cat.andreurm.blacklist.R;

public class MapFragment extends Fragment {

    public float lat;
    public float lng;
    public String address;

    public MapFragment(float la,float ln,String add){
        this.lat=la;
        this.lng=ln;
        this.address=add;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container,false);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        GoogleMap mapa = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        LatLng dest = new LatLng(lat, lng);
        CameraPosition camPos = new CameraPosition.Builder()
                .target(dest)   //Centramos el mapa en Destino
                .zoom(18)         //Establecemos el zoom en 19
                .bearing(45)      //Establecemos la orientación con el noreste arriba
                .tilt(50)         //Bajamos el punto de vista de la cámara 70 grados
                .build();

        CameraUpdate camUpd3 =
                CameraUpdateFactory.newCameraPosition(camPos);

        mapa.animateCamera(camUpd3);

        mapa.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(address));
    }


}