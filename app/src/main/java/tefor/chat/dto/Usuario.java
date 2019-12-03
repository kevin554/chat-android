package tefor.chat.dto;

public class Usuario extends DTO {

	private static Usuario instancia;
	private int codigoID;
	private String nick;
	private String contrasena;
	private String token;

    public Usuario() {
	}

	public Usuario(int codigoID, String nick, String contrasena, String token) {
		this.codigoID = codigoID;
		this.nick = nick;
		this.contrasena = contrasena;
		this.token = token;
	}

	// <editor-fold defaultstate="collapsed" desc="getters y setters">

    public static Usuario getInstancia() {
        return instancia;
    }

    public static void setInstancia(Usuario instancia) {
        Usuario.instancia = instancia;
    }

	public int getCodigoID() {
		return codigoID;
	}

	public void setCodigoID(int codigoID) {
		this.codigoID = codigoID;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

    // </editor-fold>

    @Override
    public String toString() {
		return "Usuario{" +
				"codigoID=" + codigoID +
				",nick=" + nick +
				",contrasena=" + contrasena +
				",token=" + token +
				'}';
     }

}