package com.caston.base_on_spring_boot.designpatterns.combination;

public class Client {
    public static void main(String[] args) {
        Component moumou = new University("某某大学", "文科大学");
        Component zhexue = new College("哲学系", "哲学气息拉满");
        Component faxue = new College("法学院", "专注法律知识");
        Component renwen = new Department("人文哲学", "研究人文");
        Component lishi = new Department("历史哲学", "研究历史");
        Component xingfa = new Department("刑法", "研究刑法");
        Component daode = new Department("道德法", "研究道德法");
        faxue.add(xingfa).add(daode);
        zhexue.add(renwen).add(lishi);
        moumou.add(faxue).add(zhexue);
        moumou.print();

        Component qinghua = new University("清华大学", "理科大学");
    }
}
