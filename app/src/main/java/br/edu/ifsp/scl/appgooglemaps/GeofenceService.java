package br.edu.ifsp.scl.appgooglemaps;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceService extends IntentService {

    private static final int NOTIFICATION_ID = 1;

    public GeofenceService() {
        super(GeofenceService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // Se existe erro, cancela o processamento
        if (geofencingEvent.hasError()) {
            return;
        }

        // Obtem o recurso de texto a partir do tipo de transicao
        final int textRes;
        switch (geofencingEvent.getGeofenceTransition()) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                textRes = R.string.entrou_em;
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                textRes = R.string.saiu_de;
                break;
            default:
                return;
        }

        // Obtem todas as Geofences que foram ativadas
        final List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
        final NotificationManager notificationManager = getSystemService(NotificationManager.class);
        final String notificationChannelId = getString(R.string.notification_channel);

        // Cria o canal de notificação, caso seja necessário
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, getString(R.string.geofence), NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        for (final Geofence geofence : geofences) {
            // Mostra uma notificação sobre o evento de transição
            final Notification notification = new NotificationCompat.Builder(this, notificationChannelId)
                    .setSmallIcon(R.drawable.ic_location)
                    .setContentTitle(getString(R.string.geofence))
                    .setContentText(getString(textRes, geofence.getRequestId()))
                    .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(geofence.getRequestId(), NOTIFICATION_ID, notification);
        }
    }
}
