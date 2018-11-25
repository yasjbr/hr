<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=""
                     controller="vacancy"
                     action="autoCompleteVacancyRecruitmentCycle"
                     name="recruitmentCycle.id"
                     label="${message(code: 'vacancy.recruitmentCycle.label', default: 'recruitmentCycle')}"
                     values="${[[vacancy?.recruitmentCycle?.id, vacancy?.recruitmentCycle?.name]]}"/>

</el:formGroup>

<br/>

<div style="padding-right: 40px;,padding-bottom: 15px;">
    <h4 class=" smaller lighter blue">
        ${message(code: 'vacancy.workDescription.label')}</h4> <hr/></div>

<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=" isRequired"
                     controller="job"
                     action="autocomplete"
                     name="job.id"
                     id="job"
                     label="${message(code: 'vacancy.job.label', default: 'job')}"
                     values="${[[vacancy?.job?.id, vacancy?.job?.descriptionInfo?.localName]]}"/>

    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=" isRequired"
                     controller="jobType"
                     action="autocomplete"
                     name="jobType.id"
                     label="${message(code: 'vacancy.jobType.label', default: 'jobType')}"
                     values="${[[vacancy?.jobType?.id, vacancy?.jobType?.descriptionInfo?.localName]]}"/>
</el:formGroup>


<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="pcore"
            action="governorateAutoComplete"
            name="governorates" id="governorates"
            label="${message(code: 'vacancy.governorateId.label', default: 'governorates')}"
            values="${vacancy?.transientData?.governorateMapList}"
            multiple="true"/>

    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="pcore"
            action="governorateAutoComplete"
            name="fromGovernorates" id="fromGovernorates"
            label="${message(code: 'vacancy.fromGovernorates.label', default: 'fromGovernorates')}"
            values="${vacancy?.transientData?.fromGovernorateMapList}"
            multiple="true"/>

</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=" isRequired"
                     controller="pcore"
                     action="educationDegreeAutoComplete"
                     name="educationDegrees"
                     label="${message(code: 'vacancy.educationDegrees.label', default: 'educationDegrees')}"
                     values="${vacancy?.transientData?.educationDegreeMapList}"
                     multiple="true"/>


    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=" "
                     controller="pcore"
                     action="educationMajorAutoComplete"
                     name="educationMajors"
                     label="${message(code: 'vacancy.educationMajors.label', default: 'educationMajors')}"
                     values="${vacancy?.transientData?.educationMajorMapList}"
                     multiple="true"/>
</el:formGroup>


<el:formGroup>
    <el:integerField name="numberOfPositions"
                     size="6"
                     class=" isRequired isNumber"
                     label="${message(code: 'vacancy.numberOfPositions.label', default: 'numberOfPositions')}"
                     value="${vacancy?.numberOfPositions}"/>



    <el:modalLink
            link="${createLink(controller: 'vacancy', action: 'getTheSameJobRequisitionName')}"
            preventCloseOutSide="true" class=" btn btn-sm btn-primary  icon-list"
            label="">
    </el:modalLink>






    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=""
                     controller="militaryRank"
                     action="autocomplete"
                     name="proposedRank.id"
                     label="${message(code: 'vacancy.proposedRank.label', default: 'proposedRank')}"
                     values="${[[vacancy?.proposedRank?.id, vacancy?.proposedRank?.descriptionInfo?.localName]]}"/>

</el:formGroup>

<el:formGroup>
    <el:dateField name="fulfillFromDate"
                  size="6"
                  class=" isRequired"
                  label="${message(code: 'vacancy.fulfillFromDate.label', default: 'fulfillFromDate')}"
                  value="${vacancy?.fulfillFromDate}"/>

    <el:dateField name="fulfillToDate"
                  size="6"
                  class=" "
                  label="${message(code: 'vacancy.fulfillToDate.label', default: 'fulfillToDate')}"
                  value="${vacancy?.fulfillToDate}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=""
            controller="pcore"
            action="maritalStatusAutoComplete"
            name="maritalStatusId"
            label="${message(code: 'vacancy.maritalStatusId.label', default: 'maritalStatusId')}"
            values="${[[vacancy?.maritalStatusId, vacancy?.transientData?.maritalStatus?.descriptionInfo?.localName]]}"/>
    <el:select valueMessagePrefix="EnumSexAccepted"
               from="${ps.gov.epsilon.hr.enums.jobRequisition.v1.EnumSexAccepted.values()}"
               name="sexTypeAccepted"
               id="sexTypeAccepted"
               size="6"
               class=" "
               label="${message(code: 'vacancy.sexTypeAccepted.label', default: 'sexTypeAccepted')}"
               value="${vacancy?.sexTypeAccepted}"/>
</el:formGroup>
<el:formGroup class="inspectionCategoriesDiv">
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=""
                     controller="inspectionCategory"
                     action="autocomplete"
                     paramsGenerateFunction="InspectionCategoriesParams"
                     name="inspectionCategories"
                     id="inspectionCategories"
                     label="${message(code: 'jobRequisition.inspectionCategories.label', default: 'inspectionCategories')}"
                     values="${vacancy?.inspectionCategories.collect { [it.id, it.descriptionInfo.localName] }}"
                     multiple='true'/>

    <el:textArea name="jobDescription"
                 size="6"
                 class=""
                 label="${message(code: 'vacancy.jobDescription.label', default: 'jobDescription')}"
                 value="${vacancy?.jobDescription}"/>
</el:formGroup>

<el:row/>


<div style="padding-right: 40px;,padding-bottom: 15px;">
    <h4 class=" smaller lighter blue">${message(code: 'vacancy.otherRequirements.label')}</h4> <hr/></div>

<div class="form-group ">
    <div class="col-sm-6 pcp-form-control ">
        <label class="col-sm-4 control-label no-padding-right text-left">
            ${message(code: 'vacancy.Age.label', default: 'Age')}
        </label>

        <div class="col-sm-8">
            <div id="age" class="input-group">
                <input id="fromAge"
                       value="${vacancy?.fromAge}"
                       class="form-control isNumber input-integer null"
                       type="text"
                       name="fromAge">
                <span class="input-group-addon">
                    <i class="ace-icon icon-sort-numeric"></i>
                </span>
                <input id="toAge"
                       value="${vacancy?.toAge}"
                       class="form-control isNumber input-integer"
                       type="text"
                       name="toAge">
            </div>
        </div>
    </div>


    <div class="col-sm-6 pcp-form-control ">
        <label class="col-sm-4 control-label no-padding-right text-left">
            ${message(code: 'jobRequisition.height.label', default: 'Tall')}
        </label>

        <div class="col-sm-8">
            <div id="tall" class="input-group">
                <input id="fromTall"
                       value="${vacancy?.fromTall}"
                       class="form-control isDecimal input-decimal null"
                       type="text"
                       name="fromTall">
                <span class="input-group-addon">
                    <i class="ace-icon icon-sort-numeric-outline"></i>
                </span>
                <input id="toTall"
                       value="${vacancy?.toTall}"
                       class="form-control isDecimal input-decimal"
                       type="text"
                       name="toTall">
            </div>
        </div>
    </div>

</div>

<div class="form-group ">
    <div class="col-sm-6 pcp-form-control ">
        <label class="col-sm-4 control-label no-padding-right text-left">
            ${message(code: 'vacancy.Weight.label', default: 'Weight')}
        </label>

        <div class="col-sm-8">
            <div id="weight" class="input-group">
                <input id="fromWeight"
                       value="${vacancy?.fromWeight}"
                       class="form-control isDecimal input-decimal null"
                       type="text"
                       name="fromWeight">
                <span class="input-group-addon">
                    <i class="ace-icon icon-sort-numeric-outline"></i>
                </span>
                <input id="toWeight"
                       value="${vacancy?.toWeight}"
                       class="form-control isDecimal input-decimal"
                       type="text"
                       name="toWeight">
            </div>
        </div>
    </div>

    <el:textField name="note"
                  size="6"
                  class=""
                  label="${message(code: 'vacancy.note.label', default: 'note')}"
                  value="${vacancy?.note}"/>

</div>


<br>

<div>

    <div style="padding-right: 40px;,padding-bottom: 15px;">

        <table width="100%">
            <tr>
                <td style="width: 100%;">
                    <h4 class=" smaller lighter blue">${message(code: 'vacancy.previousWork.label')}</h4>
                </td>
                <td width="160px" align="left">
                    <button type="button" class="btn  btn-bigger  btn-sm  btn-primary  btn-round"
                            id="previousWorkBtn"
                            onclick='openPreviousWorkModal()'>
                        <g:message code="vacancy.previousWork.add.btn"/>
                    </button>
                </td>
            </tr>
        </table>
        <hr style="margin-top: 0px;margin-bottom: 6px;"/>
    </div>

    <el:formGroup>
        <div class="col-md-12" id="tableDiv">
            <lay:table styleNumber="1" id="previousWorkTable">
                <lay:tableHead title="${message(code: 'jobRequisition.workExperience.periodInYears.label')}"/>
                <lay:tableHead title="${message(code: 'jobRequisition.workExperience.professionType.label')}"/>
                <lay:tableHead title="${message(code: 'jobRequisition.workExperience.competency.label')}"/>
                <lay:tableHead title="${message(code: 'jobRequisition.workExperience.otherSpecifications.label')}"/>
                <lay:tableHead title="${message(code: 'default.actions.label')}"/>
                <g:each in="${vacancy?.requisitionWorkExperiences?.sort { it?.id }}" var="workExperience"
                        status="index">
                    <rowElement>
                    <tr id="row-${index + 1}" class='center'>

                        <td class='center'>${workExperience?.periodInYears}</td>
                        <td class='center'>${workExperience?.workExperience?.transientData?.professionTypeName}</td>
                        <td class='center'>${workExperience?.workExperience?.transientData?.competencyName}</td>
                        <td class='center'>${workExperience?.otherSpecifications}</td>
                        <td class='center'>
                            <input type='hidden' name='professionType' id='professionType-${index + 1}'
                                   value='${workExperience?.workExperience?.professionType}'/>
                            <input type='hidden' name='competency' id='competency-${index + 1}'
                                   value='${workExperience?.workExperience?.competency}'/>
                            <input type='hidden' name='periodInYears' id='periodInYears-${index + 1}'
                                   value='${workExperience?.periodInYears}'/>
                            <input type='hidden' name='otherSpecifications' id='otherSpecifications-${index + 1}'
                                   value='${workExperience?.otherSpecifications}'/>

                            <span class='delete-action'>
                                <a style='cursor: pointer;'
                                   class='red icon-trash '
                                   onclick="deleteRow(${index+1});"
                                   title='<g:message code='default.button.delete.label'/>'>
                                </a>
                            </span>
                        </td>
                    </tr>
                    </rowElement>
                </g:each>

                <g:if test="${!vacancy?.requisitionWorkExperiences}">
                    <rowElement>
                        <tr id="row-0" class='center'>
                            <td class='center'></td>
                            <td class='center'></td>
                            <td class='center'></td>
                            <td class='center'></td>
                            <td class='center'></td>
                        </tr>
                    </rowElement>
                </g:if>

            </lay:table>
        </div>
    </el:formGroup>

</div>
<el:modal preventCloseOutSide="true" name="previousWorkModal" id="previousWorkModal"
          width="50%" hideCancel="true" withAttachment="true" method="post"
          title="${g.message(code: "vacancy.previousWork.label")}">

    <el:modalButton class="btn  btn-bigger  btn-sm  btn-primary  btn-round"
                    icon="ace-icon fa fa-floppy-o"
                    onClick="addPreviousWork()"
                    id="previousWorkAddBtn"
                    message="${g.message(code: "vacancy.previousWork.add.label")}"/>

    <el:modalButton calss="btn  btn-bigger  btn-sm  btn-light  btn-round"
                    id="previousWorkCancelBtn"
                    icon="ace-icon fa icon-cancel"
                    onClick="closePreviousWorkModal()"
                    message="${g.message(code: "vacancy.previousWork.modal.close.label")}"/>

    <msg:modal/>

    <el:formGroup>
        <el:integerField name="periodInYears_"
                         size="12"
                         class="isNumber isRequired"
                         label="${message(code: 'vacancy.workExperience.periodInYears.label', default: 'workExperience')}"/>
    </el:formGroup>

    <el:formGroup>
        <el:autocomplete optionKey="id"
                         optionValue="name"
                         size="12"
                         class=" "
                         controller="pcore"
                         action="professionTypeAutoComplete"
                         name="workExperience.professionType"
                         label="${message(code: 'vacancy.workExperience.professionType.label', default: 'workExperience')}"/>
    </el:formGroup>

    <el:formGroup>
        <el:autocomplete optionKey="id"
                         optionValue="name"
                         size="12"
                         class=" "
                         controller="pcore"
                         action="competencyAutoComplete"
                         name="workExperience.competency"
                         label="${message(code: 'vacancy.workExperience.competency.label', default: 'workExperience')}"/>
    </el:formGroup>

    <el:formGroup>
        <el:textArea name="otherSpecifications_"
                     size="12"
                     class=""
                     label="${message(code: 'vacancy.workExperience.otherSpecifications.label', default: 'workExperience')}"/>
    </el:formGroup>
</el:modal>

<script>
    $('#modal-form').on('shown.bs.modal', function () {
        $("#jobRequisitionIdTitle").val($("#jobRequisitionId").val());
        _dataTables['jobRequisitionTable1'].draw();
    });


    $("#job").on("select2:close", function (e) {
        if ($("#job").val() > 0) {
            $.ajax({
                url: '${createLink(controller: 'jobRequisition',action: 'getJobInformation')}',
                type: 'POST',
                data: {id: $("#job").val()},
                dataType: 'json',
                beforeSend: function (jqXHR, settings) {
                    guiLoading.show();
                },
                error: function (jqXHR) {
                    guiLoading.hide();
                },
                success: function (json) {
                    var resetNewOption = new Option('', '', true, true);
                    $('#educationDegrees').text(resetNewOption);
                    $('#educationDegrees').trigger('change');
                    /* to set education degrees*/
                    if (json.job.transientData.educationDegreeMapList) {
                        $("#educationDegrees").val(json.job.transientData.educationDegreeMapList);
                        for (indx = 0; indx < json.job.transientData.educationDegreeMapList.length; indx++) {
                            var newOption = new Option(json.job.transientData.educationDegreeMapList[indx][1], json.job.transientData.educationDegreeMapList[indx][0], true, true);
                            $('#educationDegrees').append(newOption);
                            $('#educationDegrees').trigger('change');
                            educationDegreeList.push(json.job.transientData.educationDegreeMapList[indx][1])
                        }
                    }
                    removeCloseBtnFromEducationDegree();


                    /* to set education Majors*/
                    $('#educationMajors').text(resetNewOption);
                    $('#educationMajors').trigger('change');
                    if (json.job.transientData.educationMajorMapList) {
                        $("#educationMajors").val(json.job.transientData.educationMajorMapList);
                        for (indx = 0; indx < json.job.transientData.educationMajorMapList.length; indx++) {
                            var newOption = new Option(json.job.transientData.educationMajorMapList[indx][1], json.job.transientData.educationMajorMapList[indx][0], true, true);
                            $('#educationMajors').append(newOption);
                            $('#educationMajors').trigger('change');
                            educationMajorList.push(json.job.transientData.educationMajorMapList[indx][1])
                        }
                    }
                    removeCloseBtnFromEducationMajor();


                    var $el = $("#inspectionCategories");
                    $el.text(resetNewOption);
                    $el.trigger('change');
                    /* to set inspection categories*/
                    if (json.job.transientData.inspectionCategoryMapList) {
                        $("#inspectionCategories").val(json.job.transientData.inspectionCategoryMapList);

                        var newOption = new Option("", "", true, true);
                        $el.text(newOption);
                        $el.trigger('change');

                        for (indx = 0; indx < json.job.transientData.inspectionCategoryMapList.length; indx++) {
                            var id = json.job.transientData.inspectionCategoryMapList[indx][0];
                            var text = json.job.transientData.inspectionCategoryMapList[indx][1] + " (إلزامي) ";
                            var newOption = new Option(text, id, true, true);
                            mandatoryInspection.push(text);
                            $el.append(newOption);
                            $el.trigger('change');

                        }
                    }
                    /*to set mandatory inspection */
                    if (json.inspections.length > 0) {
                        for (indx = 0; indx < json.inspections.length; indx++) {
                            var id = json.inspections[indx].id;
                            var text = json.inspections[indx].text + " (إلزامي) ";
                            var newOption = new Option(text, id, true, true);
                            mandatoryInspection.push(text);
                            $el.append(newOption);
                            $el.trigger('change');
                        }
                    }


                    /*to set from age to age */
                    $('#fromAge').text(resetNewOption);
                    $('#fromAge').trigger('change');
                    $("#fromAge").val(json.job.fromAge);
                    var newOption = new Option(json.job.fromAge, true);
                    $('#fromAge').append(newOption);
                    $('#fromAge').trigger('change');


                    $('#toAge').text(resetNewOption);
                    $('#toAge').trigger('change');
                    $("#toAge").val(json.job.toAge);
                    var newOption = new Option(json.job.toAge, true);
                    $('#toAge').append(newOption);
                    $('#toAge').trigger('change');


                    /*to set from weight to weight*/
                    $('#fromWeight').text(resetNewOption);
                    $('#fromWeight').trigger('change');
                    $("#fromWeight").val(json.job.fromWeight);
                    var newOption = new Option(json.job.fromWeight, true);
                    $('#fromWeight').append(newOption);
                    $('#fromWeight').trigger('change');

                    $('#toWeight').text(resetNewOption);
                    $('#toWeight').trigger('change');
                    $("#toWeight").val(json.job.toWeight);
                    var newOption = new Option(json.job.toWeight, true);
                    $('#toWeight').append(newOption);
                    $('#toWeight').trigger('change');


                    removeCloseBtn();
                    guiLoading.hide();
                }
            });
        }
    });

    $("#inspectionCategories").on("select2:close", function (e) {
        removeCloseBtn();
    });


</script>