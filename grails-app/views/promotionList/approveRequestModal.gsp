<el:validatableModalForm title="${message(code: 'list.approveRequest.label')}"
                         width="60%"
                         name="sendDataForm"
                         controller="promotionList"
                         hideCancel="true"
                         hideClose="true"
                         action="changeRequestToApproved" callBackFunction="callBackFunction">
    <el:hiddenField name="encodedId" id="encodedId" value="${promotionList?.encodedId}"></el:hiddenField>
    <msg:modal/>
    <msg:warning label="${message(code: 'dispatchList.approve.warning.message', default: 'Warning')}" />
    <el:formGroup>
        <el:textField name="code"
                      size="6"
                      class=" "
                      label="${message(code: 'promotionList.code.label', default: 'code')}"
                      value="${promotionList?.code}"
                      isReadOnly="true"/>

        <el:textField name="name"
                      size="6"
                      class=" "
                      label="${message(code: 'promotionList.name.label', default: 'name')}"
                      value="${promotionList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="6"
                      class=" "
                      label="${message(code: 'promotionList.trackingInfo.dateCreatedUTC.label')}"
                      value="${promotionList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>

        <el:dateField name="toDate"
                      size="6"
                      class=" "
                      label="${message(code: 'promotionList.transientData.receiveDate.label')}"
                      value="${promotionList?.transientData?.receiveDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="orderNo" size="6" class=" isRequired" label="${message(code: 'employeePromotion.managerialOrderNumber.label', default: 'orderNo')}" value="${promotionListEmployee?.managerialOrderNumber}"/>
        <el:dateField name="orderDate" size="6" class=" isRequired" label="${message(code: 'employeePromotion.orderDate.label')}" value="${promotionListEmployee?.orderDate}"/>
    </el:formGroup>

    <g:render template="recordAcceptForm" model="[listEmployee:promotionListEmployee]"/>

    <el:row/>

    <el:hiddenField name="checked_promotionEmployeeIdsList" value="" />

    <el:formButton isSubmit="true" functionName="save"/>

    <el:row/>

</el:validatableModalForm>

<script type="text/javascript">
    $("#checked_promotionEmployeeIdsList").val(_dataTablesCheckBoxValues['promotionListEmployeeTableInPromotionList']);
    function callBackFunction(json) {
        if (json.success) {
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }

    function militaryRankParams() {
        return {"greaterThanOrderNo" : "${promotionListEmployee?.employee?.currentEmployeeMilitaryRank?.militaryRank?.orderNo}"}
    }
</script>