<el:hiddenField name="employee.id" value="${childRequest?.employee?.id}"/>
<g:if test="${!hideEmployeeInfo}">
    <g:render template="/employee/wrapperForm" model="[employee: childRequest?.employee]"/>
</g:if>
<br/>
<lay:widget transparent="true" color="blue" icon="icon-home"
            title="${g.message(code: "childRequest.info.label")}">
    <lay:widgetBody>
        <el:formGroup>
            <el:dateField name="requestDate" size="6" class=" isRequired"
                          isMaxDate="true"
                          value="${childRequest?.requestDate}"
                          label="${message(code: 'childRequest.requestDate.label', default: 'requestDate')}"/>

            <el:checkboxField label="${message(code: 'childRequest.isDependent.label', default: 'isDependent')}"
                              size="6"
                              name="isDependent"
                              value="${childRequest?.isDependent}"
                              isChecked="${childRequest?.isDependent}"/>
        </el:formGroup>
        <el:formGroup>
            <el:textArea type="text" name="requestStatusNote" size="6" class=""
                         label="${message(code: 'childRequest.requestStatusNote.label', default: 'requestStatusNote')}"
                         value="${childRequest?.requestStatusNote}"/>
        </el:formGroup>
    </lay:widgetBody>
</lay:widget>


<br/>
<lay:widget transparent="true" color="blue" icon="icon-home"
            title="${g.message(code: "childRequest.relatedPerson.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:autocomplete
                    optionKey="id"
                    optionValue="name"
                    size="6"
                    class=" isRequired"
                    name="relatedPersonId"
                    controller="person" disabled="${params.action == "editRequestCreate"}"
                    action="autocomplete"
                    label="${message(code: 'childRequest.transientData.relatedPersonDTO.localFullName.label', default: 'relatedPerson name')}"
                    values="${[[childRequest?.relatedPersonId, childRequest?.transientData?.relatedPersonDTO?.localFullName]]}"/>

            <btn:addButton onclick="confirmAddPerson()"/>
        </el:formGroup>
        <br/>

        <div id="relatedPersonDetailsDiv"></div>


        <g:if test="${!hideManagerialOrderInfo}">
            <lay:widget transparent="true" color="green3" icon="fa fa-certificate"
                        title="${g.message(code: "request.managerialOrderInfo.label")}">
                <lay:widgetBody>
                    <g:render template="/request/wrapperManagerialOrder"
                              model="[request: childRequest, formName: 'childRequestForm', parentFolder: 'childList']"/>
                </lay:widgetBody>
            </lay:widget>
        </g:if>


        <g:if test="${workflowPathHeader}">
            <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
        </g:if>
    </lay:widgetBody>
</lay:widget>

<script type="text/javascript">
    function getPersonDetails() {
        $.ajax({
            url: '${createLink(controller: 'childRequest',action: 'getPersonDetails')}',
            type: 'POST',
            dataType: 'html',
            data: {
                "personId": $("#relatedPersonId").val()
            },
            beforeSend: function (jqXHR, settings) {
                $('.alert.page').html('');
                guiLoading.show();
            },
            error: function (jqXHR) {
                guiLoading.hide();
            },
            success: function (data) {
                guiLoading.hide();
                $("#relatedPersonDetailsDiv").html(data);
            }
        });
    }

    $("#relatedPersonId").on("select2:close", function (e) {
        var value = $('#relatedPersonId').val();
        if (value) {
            $.ajax({
                url: '${createLink(controller: 'childRequest',action: 'getPersonDetails')}',
                type: 'POST',
                dataType: 'html',
                data: {
                    "personId": value
                },
                beforeSend: function (jqXHR, settings) {
                    $('.alert.page').html('');
                    guiLoading.show();
                },
                error: function (jqXHR) {
                    guiLoading.hide();
                },
                success: function (data) {
                    guiLoading.hide();
                    $("#relatedPersonDetailsDiv").html(data);
                }
            });
        }
    });

    $("#relatedPersonId").on("change", function () {
        <g:if test="${childRequest?.relatedPersonId}">
        $("#relatedPersonId").trigger("select2:close");
        </g:if>
    });

    function confirmAddPerson() {
            if(${params.action != "editRequestCreate"}){
                gui.confirm.confirmFunc("${message(code: 'person.addNew.confirm.title')}", "${message(code: 'person.addNew.confirm.message')}", function () {
                    window.location.href = '${createLink(controller: 'childRequest', action: 'createNewPerson')}';
                });
            }
    }
</script>
