package scrum.cannia;

public class SessionManager {

    private static SessionManager instance;
    private Long fundacionId;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public Long getFundacionId() {
        return fundacionId;
    }

    public void setFundacionId(Long fundacionId) {
        this.fundacionId = fundacionId;
    }
}
