package de.cimt.talendcomp.jobinstance.manage;

/**
 * 64:    Das oberste Bit wird immer 0 gesetzt um eine positive Ganzzahl zu behalten
 * 63-17: die letzten 46 Bits der Unix Time in ms
 * 16-1:  Sequence innerhalb der Millisekunde mit zufälligem Offset im Bereich von 0-1000
 * 
 * @author jan.lolling@gmail.com
 *
 */
public class JID2 {
	
	private Long startDate = null;
	private long currentMillisecond = 0;
	private long lastMillisecond = 0;
	private long jid;
	private int lastTalendPidSequenceValue = 1;
	
	public static final long mask46 =  Long.parseLong("1111111111111111111111111111111111111111111111", 2);
	public static final int mask16 = Integer.parseInt("1111111111111111", 2);
	
	public long createJID() throws Exception {
		currentMillisecond = retrieveTimeInMillis();
		// 33  bit mask
		lastTalendPidSequenceValue = setupSequenceWithingMilliSec();
		lastTalendPidSequenceValue = lastTalendPidSequenceValue & mask16;
		jid = currentMillisecond & mask46;
		jid = jid << 16;
		jid = jid | lastTalendPidSequenceValue;
		lastMillisecond = currentMillisecond;
		return jid;
	}
	
	public long getTimePart(long jobInstanceId) {
		return jobInstanceId >> 16;
	}
	
	private long retrieveTimeInMillis() {
		if (startDate != null) {
			return startDate;
		} else {
			return System.currentTimeMillis();
		}
	}

	public static String longToString(long number) {
	    StringBuilder result = new StringBuilder();
	    for (int i = 63; i >= 0 ; i--) {
	        long mask = 1l << i;
	        result.append((number & mask) != 0 ? "1" : "0");
	        if (i % 4 == 0) {
	            result.append(" ");
	        }
	    }
	    result.replace(result.length() - 1, result.length(), "");
	    return result.toString();
	}
	
	public long getJID() {
		return jid;
	}
	
	@Override
	public String toString() {
		return jid + " bits: " + longToString(jid);
	}

	public void setStartDate(Long startDate) {
		if (startDate != null) {
			this.startDate = startDate;
		}
	}

	public synchronized int setupSequenceWithingMilliSec() throws InterruptedException {
		if (lastMillisecond < currentMillisecond) {
			lastTalendPidSequenceValue = (int) (Math.random() * 1000);
		} else {
			lastTalendPidSequenceValue = lastTalendPidSequenceValue + 1;
		}
		if (lastTalendPidSequenceValue >= mask16) {
			Thread.sleep(1);
			currentMillisecond = retrieveTimeInMillis();
			lastTalendPidSequenceValue = 1;
		}
		return lastTalendPidSequenceValue;
	}

}