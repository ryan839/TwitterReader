package com.hex.hackathon.TwitterReader.Beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MentionsBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<MentionBean> mentions;


	public MentionsBean() {
		super();
	}

	public MentionsBean(ArrayList<MentionBean> mentions) {
		super();
		this.mentions = mentions;
	}

	@Override
	public String toString() {
		return "mentionsBean [mentions=" + mentions + "]";
	}

	public List<MentionBean> getmentions() {
		return mentions;
	}

	public void setmentions(ArrayList<MentionBean> mentions) {
		this.mentions = mentions;
	}
}
