package aiku_main.oauth.error;

public interface KauthBaseErrorCode {
    public KauthErrorReason getErrorReason();

    String getExplainError() throws NoSuchFieldException;

}
