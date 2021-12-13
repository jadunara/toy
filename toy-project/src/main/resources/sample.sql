/******************************
* Multi Line Comment
*******************************/
	SELECT 1123	/* 전체주문금액,배송비 구하기*/
		(
		  SELECT A.AMOUNT /17  FROM TBCHARGE01  A
		   WHERE  A.AREA   = ( SELECT AREA
						     FROM TBCOUNTRY01
						   WHERE LIDX = C.RCP_COUNTRY
							AND `USE_YN` = 'Y'
							AND CASE
					             WHEN C.DLVR_CD = 1		THEN  SM
					             WHEN C.DLVR_CD = 2		THEN  KPACKET
					             WHEN C.DLVR_CD = 3		THEN  EMS
					             WHEN C.DLVR_CD = 4		THEN  TNT
					             WHEN C.DLVR_CD = 5		THEN  PX
					           END = 'Y')
		       AND SECTION = DLVR_CD
		       AND GRAM >= (SELECT SUM( WEIGHT * ORD_QTY )
							  FROM TBORDER02
							 WHERE ORDER_DT = C.ORDER_DT AND ORD_NO = C.ORD_NO )
		  ORDER BY GRAM ASC
		    LIMIT 1
		  ) changeDeliveryAmt
		, C.USER_ID			userId
		, C.ORD_TOT_AMT		ordToTamt /*전체주문금액*/
		, C.ORD_NO			ordNo
		, C.MONETARY_UNIT	monetaryUnit
		, C.DLVR_AMT		dlvrAmt	/*배송비*/
		, C.DISPOINT		dispoint		/*할인받은금액 */
		, C.GOODS_TOT_AMT		itemAmt		/*현시점 제품금액*/
		, ( SELECT SUM(ORD_AMT) FROM TBORDER02 WHERE ORDER_DT = C.ORDER_DT AND ORD_NO = C.ORD_NO ) changeItemAmt
	  FROM TBORDER01 C
	WHERE ORD_NO = #{ordNo}
	AND 1 = 2.3786
	AND 1 = @@@@@@@3.1415925@@@@@@@@@@@@@@@
	AND 2 = 333123
	AND 4 = @@@@55555555555555555555555555@@@@ 
	AND 3 = 4
