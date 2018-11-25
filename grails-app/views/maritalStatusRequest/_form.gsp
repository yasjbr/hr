<el:hiddenField name="employee.id" value="${maritalStatusRequest?.employee?.id}"/>
<g:if test="${!hideEmployeeInfo}">
    <g:render template="/employee/wrapperForm" model="[employee: maritalStatusRequest?.employee, isMSRequest: true]"/>
</g:if>
<g:if test="${maritalStatusRequest?.employee?.transientData?.personDTO?.genderType?.id == ps.police.pcore.enums.v1.GenderType.FEMALE.value()}">
    <g:set var="relatedPersonTitle" value="${message(code: 'maritalStatusRequest.currentHusband.label')}"/>
</g:if>
<g:else>
    <g:set var="relatedPersonTitle" value="${message(code: 'maritalStatusRequest.currentWife.label')}"/>
</g:else>
<lay:widget transparent="true" color="blue" icon="icon-heart-6"
            title="${relatedPersonTitle}">
    <lay:widgetBody>
        <div class="col-md-12" id="tableDiv">
            <lay:table styleNumber="1" id="relatedPersonTable">
                <lay:tableHead title="${message(code: 'employee.personName.label')}"/>
                <lay:tableHead title="${message(code: 'person.recentCardNo.label')}"/>
                <lay:tableHead title="${message(code: 'person.localMotherName.label')}"/>
                <lay:tableHead title="${message(code: 'default.actions.label')}"/>
                <g:each in="${maritalStatusRequest?.transientData?.relatedPersonList?.sort { it?.id }}"
                        var="relatedPerson">
                    <rowElement>
                        <tr id="row" class='center'>
                            <td class='center'>${relatedPerson?.localFullName}</td>
                            <td class='center'>${relatedPerson?.recentCardNo}</td>
                            <td class='center'>${relatedPerson?.localMotherName}</td>
                            <td class='center'>
                                <span class='select2-results'>
                                    <a style='cursor: pointer;'
                                       class='infobox-black icon-up-hand'
                                       onclick="selectRelatedPerson('${relatedPerson?.id}', '${relatedPerson?.localFullName}')"
                                       title="<g:message code='default.button.select.label'/>">
                                    </a>
                                </span>
                            </td>
                        </tr>
                    </rowElement>
                </g:each>
            </lay:table>
        </div>
    </lay:widgetBody>
</lay:widget>

<el:row/>
<el:row/>
<el:row/>
<br/>
<br/>

<lay:widget transparent="true" color="blue" icon="icon-home"
            title="${g.message(code: "maritalStatusRequest.info.label")}">
    <lay:widgetBody>
        <el:formGroup>
            <el:dateField name="requestDate" size="6" class=" isRequired"
                          isMaxDate="true" disabled="${params.action == "editRequestCreate"}"
                          value="${maritalStatusRequest?.requestDate}"
                          label="${message(code: 'maritalStatusRequest.requestDate.label', default: 'requestDate')}"/>
            <el:autocomplete
                    optionKey="id"
                    optionValue="name"
                    size="6"
                    class=" isRequired"
                    controller="pcore"
                    action="maritalStatusAutoComplete"
                    name="oldMaritalStatusId" disabled="${params.action == "editRequestCreate"}"
                    id="oldMaritalStatusId"
                    label="${message(code: 'maritalStatusRequest.oldMaritalStatusId.label', default: 'oldMaritalStatusId')}"
                    values="${[[maritalStatusRequest?.oldMaritalStatusId, maritalStatusRequest?.transientData?.oldMaritalStatusName]]}"/>
        </el:formGroup>


        <el:formGroup>

            <el:autocomplete
                    optionKey="id"
                    optionValue="name"
                    size="6"
                    class=" isRequired"
                    controller="maritalStatusRequest"
                    action="maritalStatusAutocomplete"
                    paramsGenerateFunction="newMaritalStatusParams"
                    name="newMaritalStatusId" disabled="${params.action == "editRequestCreate"}"
                    label="${message(code: 'maritalStatusRequest.newMaritalStatusId.label', default: 'newMaritalStatusId')}"
                    values="${[[maritalStatusRequest?.newMaritalStatusId, maritalStatusRequest?.transientData?.newMaritalStatusName]]}"/>

            <el:dateField name="maritalStatusDate" size="6" class=" isRequired"
                          isMaxDate="true"
                          value="${maritalStatusRequest?.maritalStatusDate}"
                          label="${message(code: 'maritalStatusRequest.maritalStatusDate.label', default: 'maritalStatusDate')}"/>
        </el:formGroup>

        <el:formGroup>
            <el:checkboxField label="${message(code: 'maritalStatusRequest.isDependent.label', default: 'isDependent')}"
                              size="6"
                              name="isDependent"
                              value="${maritalStatusRequest?.isDependent}"
                              isChecked="${maritalStatusRequest?.isDependent}" isDisabled="${params.action == "editRequestCreate"}"/>
            <el:textArea type="text" name="requestStatusNote" size="6" class=""
                         label="${message(code: 'maritalStatusRequest.requestStatusNote.label', default: 'requestStatusNote')}"
                         value="${maritalStatusRequest?.requestStatusNote}"/>
        </el:formGroup>
    </lay:widgetBody>
</lay:widget>
<br/>

<lay:widget transparent="true" color="blue" icon="icon-home"
            title="${g.message(code: "maritalStatusRequest.relativePersonInfo.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:autocomplete
                    optionKey="id"
                    optionValue="name"
                    size="6"
                    class=" isRequired"
                    paramsGenerateFunction="paramsForPersonAutocomplete"
                    controller="maritalStatusRequest"
                    action="personAutocomplete" disabled="${params.action == "editRequestCreate"}"
                    name="relatedPersonId"
                    label="${message(code: 'employee.searchPerson.label', default: 'employee')}"
                    values="${[[maritalStatusRequest?.relatedPersonId, maritalStatusRequest?.transientData?.relatedPersonDTO?.localFullName]]}"/>

            <btn:addButton onclick="confirmAddPerson()"/>
        </el:formGroup>
        <br/>

        <div id="relatedPersonDetailsDiv"></div>

    </lay:widgetBody>
</lay:widget>
<el:row/>


<g:if test="${!hideManagerialOrderInfo}">
    <lay:widget transparent="true" color="green3" icon="fa fa-certificate"
                title="${g.message(code: "request.managerialOrderInfo.label")}">
        <lay:widgetBody>
            <g:render template="/request/wrapperManagerialOrder"
                      model="[request: maritalStatusRequest, formName: 'maritalStatusRequestForm', parentFolder: 'maritalStatusList']"/>
        </lay:widgetBody>
    </lay:widget>
</g:if>

<g:if test="${workflowPathHeader}">
    <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
</g:if>


<script type="text/javascript">
    function getPersonDetails() {
        $.ajax({
            url: '${createLink(controller: 'maritalStatusRequest',action: 'getPersonDetails')}',
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
                url: '${createLink(controller: 'maritalStatusRequest',action: 'getPersonDetails')}',
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
        <g:if test="${maritalStatusRequest?.relatedPersonId}">
        $("#relatedPersonId").trigger("select2:close");
        </g:if>
    });

    function confirmAddPerson() {
        if (${params.action != "editRequestCreate"}) {
            gui.confirm.confirmFunc("${message(code: 'person.addNew.confirm.title')}", "${message(code: 'person.addNew.confirm.message')}", function () {
                window.location.href = '${createLink(controller: 'maritalStatusRequest', action: 'createNewPerson')}';
            });
        }
    }

    function selectRelatedPerson(relatedPersonId, relatedPersonName) {
        var newOption = new Option(relatedPersonName, relatedPersonId, true, true);
        $('#relatedPersonId').append(newOption);
        $('#relatedPersonId').trigger('change');
        $("#relatedPersonId").trigger("select2:close");
    }

    function paramsForPersonAutocomplete() {
        var searchParams = {};
        searchParams.oldMaritalStatusId = $("#oldMaritalStatusId").val();
        searchParams.newMaritalStatusId = $("#newMaritalStatusId").val();
        searchParams.employeeId = "${maritalStatusRequest?.employee?.id}";
        return searchParams;
    }

    function newMaritalStatusParams() {
        return {
            "oldMaritalStatusId": $('#oldMaritalStatusId').val(),
            "employeeGenderType": "${maritalStatusRequest?.employee?.transientData?.personDTO?.genderType?.id}"
        };
    }

    <g:if test="${maritalStatusRequest?.oldMaritalStatusId}">
    $("#oldMaritalStatusId").trigger("change");
    </g:if>

</script>




