<el:modal isModalWithDiv="true" id="createRequestModal" title="${message(code: 'aocListRecord.createRequest.label', args: [requestEntityName])}"
          hideCancel="true" preventCloseOutSide="true" width="70%">
    <msg:modal/>
    <g:if test="${aocCorrespondenceList?.correspondenceType?.createSteps == "1"}">
        <el:validatableResetForm name="hrRequestForm" callLoadingFunction="performPostActionWithEncodedId"
                                 controller="aocListRecord" action="save" callBackFunction="createRecordCallBackFunction">
            <g:hiddenField name="aocCorrespondenceList.id" value="${aocCorrespondenceList?.id}"/>
            <g:hiddenField name="correspondenceType" value="${aocCorrespondenceList?.correspondenceType}"/>
            <g:hiddenField name="requestStatus" value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST}"/>
            <g:hiddenField name="firm.id" value="${firmId}"/>
            <g:render template="/${aocCorrespondenceList?.correspondenceType?.hrRequestDomain}/form" />

            <el:formButton functionName="save" withClose="true" isSubmit="true"/>
            <el:formButton functionName="cancel" onClick="createRecordCallBackFunction()"/>
        </el:validatableResetForm>
    </g:if>
    <g:else>
        <div class="request-header-details">
            <el:validatableForm title="${title}" callBackGeneralFunction="selectEmployeeSuccessCallBack" responseDataType="html"
                                name="aocSelectEmployeeForm" controller="aocListRecord" action="selectEmployee">
                <g:hiddenField name="aocCorrespondenceList.id" value="${aocCorrespondenceList?.id}" />
                <g:hiddenField name="correspondenceType" value="${aocCorrespondenceList?.correspondenceType}" />

                <g:if test="${firmId}">
                    <g:hiddenField name="firmId" value="${firmId}"/>
                </g:if>
                <g:else>
                    <el:formGroup>
                        <el:autocomplete size="6" optionKey="id" optionValue="name" class=" isRequired" controller="firm"
                                         action="autocomplete" name="firmId" onChange="handleFirmChange()"
                                         label="${message(code: 'firm.label', default: 'firm')}"/>
                    </el:formGroup>
                </g:else>

                <el:hiddenField name="checked_requestIdsList" value=""/>

                <g:render template="/${aocCorrespondenceList?.correspondenceType?.listDomain}/selectEmployeeForm"/>

                <div class="operationForm">

                </div>

                <el:formButton id="selectEmployeeButton" functionName="select" isSubmit="true"
                               onclick="beforeSelectEmployeeHandler()"/>

                <el:formButton functionName="close" onClick="createRecordCallBackFunction()"/>
            </el:validatableForm>
        </div>
        <div class="request-details" style="display: none">
            <el:validatableResetForm name="hrRequestForm" callLoadingFunction="performPostActionWithEncodedId"
                                     controller="aocListRecord" action="save" callBackFunction="createRecordCallBackFunction">
                <g:hiddenField name="aocCorrespondenceList.id" value="${aocCorrespondenceList?.id}"/>
                <g:hiddenField name="correspondenceType" value="${aocCorrespondenceList?.correspondenceType}"/>
                <g:hiddenField name="requestStatus" value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST}"/>
                <g:hiddenField id="requestFirmId" name="firm.id" value="${firmId}"/>
                <div class="request-details-form">

                </div>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="close" onClick="createRecordCallBackFunction()"/>

            </el:validatableResetForm>
        </div>
    </g:else>


</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    /**
     * Called on selectEmployee request completion
     * @param html: html frame that contains actual request content
     */
    function selectEmployeeSuccessCallBack(html) {
        $('.request-details-form').html(html);
        if(html.success==true || html.success==undefined){
            showDetailsForm();
        }
    }

    /**
     * hides header, shows request details
     */
    function showDetailsForm(){
        // init form on new content
        gui.initAllForModal.init($('.request-details-form'));
        $('.request-header-details').hide(300);
        $('.request-details').show(500);
    }

    /**
     * called on request creation completion
     * @param json
     */
    function createRecordCallBackFunction(json) {
        _dataTables['listRecordTableInAocList'].draw();
        $('#application-modal-main-content').modal("hide");
    }

    /**
     * gets an html page in ajax
     * @param params
     */
    function renderOperationsFormPage(params){
        var firmId= $('#firmId').val();
        if(params.requestCategory=='' || params.requestCategory == "${ps.gov.epsilon.hr.enums.v1.EnumRequestCategory.ORIGINAL.name()}" || firmId == ""){
            $(".operationForm").html("");
        }else{
            var link= "${createLink(controller: 'aocListRecord', action: 'operationsForm')}";
            params['correspondenceType']= $('#correspondenceType').val();
            params['firmId']= firmId;
            $.ajax({
                url: link,
                data:params,
                dataType: 'html',
                type: 'POST',
                success: function(html){
                    try {
                        // response is json, there is a problem
                        var json = $.parseJSON(html);
                        $('.modalPage').html(json.message);
                    } catch(err) {
                        // response is html, just render it
                        $(".operationForm").html(html);
                    }
                },
                error: function (xhr,status,error) {
                    alert("failed to load page " + error);
                }
            });
        }
    }

    /**
     * check selected record from table to be submitted to server
     * @returns {boolean}
     */
    function beforeSelectEmployeeHandler(){
        $("#checked_requestIdsList").val(_dataTablesCheckBoxValues['requestsTable']);
        return true;
    }

    /**
     * set firm id to be submitted within hr request data
     */
    function handleFirmChange(){
        $('#requestFirmId').val($('#firmId').val());
        gui.autocomplete.clear("employeeId");
    }
</script>