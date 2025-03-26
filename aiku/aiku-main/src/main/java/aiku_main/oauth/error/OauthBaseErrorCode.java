package aiku_main.oauth.error;

public interface OauthBaseErrorCode {
    public OauthErrorReason getErrorReason();

    String getExplainError() throws NoSuchFieldException;

}
