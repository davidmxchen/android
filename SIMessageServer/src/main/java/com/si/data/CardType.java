package com.si.data;

import com.si.transaction.api.*;
import java.io.Serializable;

/**
 * Supported payment card types.
 *
 */
public enum CardType implements Serializable {
	VISA("Visa"),
	MASTER_CARD("MasterCard"),
	AMERICAN_EXPRESS("AmericanExpress"),
	DISCOVER("Discover"),
	DINERS_CLUB("DinersClub"),
	JCB("JCB"),
	ECHECK("eCheck"),	// added for the reporting API
	UNKNOWN("");

	private final String value;

	private CardType(String value) {
		this.value = value;
	}

	public static CardType findByValue(String value) {
		if(value != null) {
			for(CardType cardType : values()) {
				if(cardType.value.equals(value)) {
					return cardType;
				}
			}
		}

		return CardType.UNKNOWN;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

}
