package login.oauth.error;

public interface KauthBaseErrorCode {
    public KauthErrorReason getErrorReason();

    String getExplainError() throws NoSuchFieldException;

}
