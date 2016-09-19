package com.example.amandafernandes.mobrec;

import android.os.Parcel;
import android.os.Parcelable;

public class Attribution implements Parcelable {
	// Attribution of the photo
	String mHtmlAttribution="";

	@Override
	public int describeContents() {		
		return 0;
	}

	/** Writing Attribution object data to Parcel */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mHtmlAttribution);		
	}
	
	public Attribution(){
		
	}
	
	/**  Initializing Attribution object from Parcel object */
	private Attribution(Parcel in){
		this.mHtmlAttribution = in.readString();
	}
	
	/** Generates an instance of Attribution class from Parcel */
	public static final Creator<Attribution> CREATOR = new Creator<Attribution>() {
		@Override
		public Attribution createFromParcel(Parcel source) {			
			return new Attribution(source);
		}

		@Override
		public Attribution[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}
	};	
}
