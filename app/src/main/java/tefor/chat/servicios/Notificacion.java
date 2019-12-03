package tefor.chat.servicios;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import tefor.chat.activities.DetalleMensajes;
import tefor.chat.activities.ListaMensajes;
import tefor.chat.dto.Usuario;

public class Notificacion extends FirebaseMessagingService {

    public static final String TIPO = "tipo";
    public static final String MENSAJE = "mensaje";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() == 0) {
            return;
        }

        if (remoteMessage.getData().containsKey(TIPO)
                && remoteMessage.getData().get(TIPO).equals(MENSAJE)) {
            String mensaje = remoteMessage.getData().get(MENSAJE);
            String nickAQuienLeLlega = remoteMessage.getData().get("nick");
            String nickRemitente = remoteMessage.getData().get("nick_destinatario");

            SharedPreferences preferencias = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
            String miNick = preferencias.getString("nick", "");

            if (miNick.isEmpty() && Usuario.getInstancia() != null) {
                miNick = Usuario.getInstancia().getNick();
            }

            if (miNick.isEmpty()) {
                return;
            }

            if (!nickAQuienLeLlega.equals(miNick)) {
                return;
            }

            Intent intent = new Intent(this, DetalleMensajes.class);
            intent.putExtra(DetalleMensajes.REMITENTE, nickRemitente);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                            .setContentTitle("NDR Message")
                            .setSmallIcon(android.R.drawable.ic_input_add)
                            .setContentText(mensaje)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationBuilder.build());

            int i = 4;
        }
    }

}
