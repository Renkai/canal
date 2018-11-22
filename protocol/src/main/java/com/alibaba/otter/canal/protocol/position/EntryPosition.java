package com.alibaba.otter.canal.protocol.position;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据库对象的唯一标示
 *
 * @author jianghang 2012-6-14 下午09:20:07
 * @version 1.0.0
 */
public class EntryPosition extends TimePosition {
    static Logger logger = LoggerFactory.getLogger(EntryPosition.class);
    private static final long serialVersionUID = 81432665066427482L;
    public static final int EVENTIDENTITY_SEGMENT = 3;
    public static final char EVENTIDENTITY_SPLIT = (char) 5;

    private boolean included = false;
    private String journalName;
    private Long position;
    // add by agapple at 2016-06-28
    private Long serverId = null;              // 记录一下位点对应的serverId
    private String gtid = null;
    private static Pattern filePattern = Pattern.compile("([^.]*\\.\\d+).*");

    public EntryPosition() {
        super(null);
    }

    public EntryPosition(Long timestamp) {
        this(null, null, timestamp);
    }

    public EntryPosition(String journalName, Long position) {
        this(journalName, position, null);
    }

    public EntryPosition(String journalName, Long position, Long timestamp) {
        super(timestamp);
        this.journalName = prettyJournalName(journalName);
        if (!journalName.equals(this.journalName)) {
            logger.debug("got journal name {} but we use {}", journalName, this.journalName);
        }
        this.position = position;
    }

    public static String prettyJournalName(String nonPrettyJournalName) {
        String tmp = nonPrettyJournalName.replaceAll("[^\\x00-\\x7F]", "");
        tmp = tmp.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
        tmp = tmp.replaceAll("\\p{C}", "");
        tmp.trim();
        try {
            Matcher matcher = filePattern.matcher(tmp);
            matcher.matches();
            return matcher.group(1);
        } catch (Exception e) {
            logger.error("name not match: " + nonPrettyJournalName);
            logger.error("name not match: " + tmp);
            return "";
        }
    }

    public EntryPosition(String journalName, Long position, Long timestamp, Long serverId) {
        this(journalName, position, timestamp);
        this.serverId = serverId;
    }

    public String getJournalName() {
        return journalName;
    }

    public void setJournalName(String journalName) {
        this.journalName = prettyJournalName(journalName);
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public boolean isIncluded() {
        return included;
    }

    public void setIncluded(boolean included) {
        this.included = included;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public String getGtid() {
        return gtid;
    }

    public void setGtid(String gtid) {
        this.gtid = gtid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((journalName == null) ? 0 : journalName.hashCode());
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        // 手写equals，自动生成时需注意
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof EntryPosition)) {
            return false;
        }
        EntryPosition other = (EntryPosition) obj;
        if (journalName == null) {
            if (other.journalName != null) {
                return false;
            }
        } else if (!journalName.equals(other.journalName)) {
            return false;
        }
        if (position == null) {
            if (other.position != null) {
                return false;
            }
        } else if (!position.equals(other.position)) {
            return false;
        }
        // 手写equals，自动生成时需注意
        if (timestamp == null) {
            if (other.timestamp != null) {
                return false;
            }
        } else if (!timestamp.equals(other.timestamp)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(EntryPosition o) {
        final int val = journalName.compareTo(o.journalName);

        if (val == 0) {
            return (int) (position - o.position);
        }
        return val;
    }

    public static void main(String[] args) {
        String hehe = prettyJournalName("mysql-bin-changelog.017003<83>^M<8b>ÿ");
        System.out.printf(hehe);
    }
}
