package ir.sample.app.PishSabteNam.models;

public class WeatherModel {
    private String todayday;
    private String todaydate;
    private String todayindex;
    private String todaytext;
    private String todayimage;
    private String first24;
    private String second24;
    private String third24;

    public WeatherModel(String todayday, String todaydate, String todayindex, String todaytext, String todayimage, String first24, String second24, String third24) {
        this.todayday = todayday;
        this.todaydate = todaydate;
        this.todayindex = todayindex;
        this.todaytext = todaytext;
        this.todayimage = todayimage;
        this.first24 = first24;
        this.second24 = second24;
        this.third24 = third24;
    }

    public String getTodayday() {
        return todayday;
    }

    public void setTodayday(String todayday) {
        this.todayday = todayday;
    }

    public String getTodaydate() {
        return todaydate;
    }

    public void setTodaydate(String todaydate) {
        this.todaydate = todaydate;
    }

    public String getTodayindex() {
        return todayindex;
    }

    public void setTodayindex(String todayindex) {
        this.todayindex = todayindex;
    }

    public String getTodaytext() {
        return todaytext;
    }

    public void setTodaytext(String todaytext) {
        this.todaytext = todaytext;
    }

    public String getTodayimage() {
        return todayimage;
    }

    public void setTodayimage(String todayimage) {
        this.todayimage = todayimage;
    }

    public String getFirst24() {
        return first24;
    }

    public void setFirst24(String first24) {
        this.first24 = first24;
    }

    public String getSecond24() {
        return second24;
    }

    public void setSecond24(String second24) {
        this.second24 = second24;
    }

    public String getThird24() {
        return third24;
    }

    public void setThird24(String third24) {
        this.third24 = third24;
    }
}
