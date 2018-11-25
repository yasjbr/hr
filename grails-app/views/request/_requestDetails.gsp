<div id="requestStatusId" style="display: none;">
    <el:formGroup>
        <el:select valueMessagePrefix="EnumRequestStatus" id="enumRequestStatusId"
                   from="${[ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED, ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED]}"
                   name="requestStatus" size="6" class=" isRequired" noSelection="true"
                   label="${message(code: 'periodSettlementRequest.requestStatus.label', default: 'requestStatus')}"
                   onChange="showHideAcceptForm()"/>
    </el:formGroup>
</div>

<div id="acceptForm" style="display:none">
    %{--accept form will be rendered here in case of approve choice is selected--}%
    <g:if test="${parentFolder}">
        <g:render template="/${parentFolder}/recordAcceptForm" model="[colSize: 8]"/>
    </g:if>
</div>

<script>
    /* gets an html page in ajax
    * @param params
    */
    function showHideAcceptForm() {
        var status = $("#requestStatus").val();
        var formDiv = $("#acceptForm");
        if (status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED.name()}") {
            formDiv.show(500);
        } else {
            formDiv.hide(100);
            ("#requestStatusId").hide(100);
        }
    }



</script>