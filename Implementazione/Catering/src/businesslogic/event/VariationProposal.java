package businesslogic.event;

import businesslogic.recipe.KitchenTask;

public class VariationProposal {
    private String comment;
    private String accepted;
    private Service srv;
    private KitchenTask rcp;

    public VariationProposal(Service srv, KitchenTask rcp, String cm) {
        this.srv = srv;
        this.rcp = rcp;
        this.comment = cm;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public Service getSrv() {
        return srv;
    }

    public void setSrv(Service srv) {
        this.srv = srv;
    }

    public KitchenTask getRcp() {
        return rcp;
    }

    public void setRcp(KitchenTask rcp) {
        this.rcp = rcp;
    }
}
