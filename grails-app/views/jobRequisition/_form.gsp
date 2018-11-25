<br/>

<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=""
                     controller="jobRequisition"
                     action="autoCompleteOpenedRecruitmentCycle"
                     name="recruitmentCycle.id"
                     label="${message(code: 'jobRequisition.recruitmentCycle.label', default: 'recruitmentCycle')}"
                     values="${[[jobRequisition?.recruitmentCycle?.id, jobRequisition?.recruitmentCycle?.name]]}"/>
    <sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value}">
        <el:autocomplete optionKey="id"
                         optionValue="name"
                         size="6"
                         class=" isRequired"
                         paramsGenerateFunction="getDepartmentParams"
                         controller="department"
                         action="autocomplete"
                         name="requestedForDepartment.id"
                         label="${message(code: 'jobRequisition.requestedForDepartmentName.label', default: 'Department')}"
                         values="${[[jobRequisition?.requestedForDepartment?.id, jobRequisition?.requestedForDepartment?.descriptionInfo?.localName]]}"/>
    </sec:ifAnyGranted>
</el:formGroup>

<g:set var="isReadOnly"
       value="${grails.plugin.springsecurity.SpringSecurityUtils.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)?.toString() == "true" ? "false" : "true"}"/>

<el:formGroup>
    <el:dateField name="requestDate"
                  isReadOnly="${isReadOnly}"
                  size="6"
                  isMaxDate="true"
                  class=" isRequired"
                  label="${message(code: 'jobRequisition.requestDate.label', default: 'requestDate')}"
                  value="${type == "edit" ? jobRequisition?.requestDate : currentDate}"/>
</el:formGroup>

<br>

<lay:widget transparent="true" color="blue"
            title="${g.message(code: "jobRequisition.workDescription.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:autocomplete optionKey="id"
                             optionValue="name"
                             size="6"
                             class=" isRequired"
                             controller="job"
                             action="autocomplete"
                             name="job.id"
                             id="jobId"
                             label="${message(code: 'jobRequisition.job.label', default: 'job')}"
                             values="${[[jobRequisition?.job?.id, jobRequisition?.job?.descriptionInfo?.localName]]}"/>

            <el:autocomplete optionKey="id"
                             optionValue="name"
                             id="jobAutoComplete"
                             size="6"
                             class=" isRequired"
                             controller="jobType"
                             action="autocomplete"
                             name="jobType.id"
                             label="${message(code: 'jobRequisition.jobType.label', default: 'jobType')}"
                             values="${[[jobRequisition?.jobType?.id, jobRequisition?.jobType?.descriptionInfo?.localName]]}"/>
        </el:formGroup>

        <el:formGroup>
            <el:autocomplete optionKey="id"
                             optionValue="name"
                             size="6"
                             class=" isRequired"
                             controller="employmentCategory"
                             action="autocomplete"
                             name="employmentCategory.id"
                             label="${message(code: 'jobRequisition.employmentCategory.label', default: 'employmentCategory')}"
                             values="${[[jobRequisition?.employmentCategory?.id, jobRequisition?.employmentCategory?.descriptionInfo?.localName]]}"/>
            <el:integerField name="numberOfPositions"
                             size="6"
                             class=" isRequired isNumber"
                             label="${message(code: 'jobRequisition.numberOfPositions.label', default: 'numberOfPositions')}"
                             value="${jobRequisition?.numberOfPositions}"/>
        </el:formGroup>

        <el:formGroup>
            <div class="educationDegreesDiv">
                <el:autocomplete optionKey="id"
                                 optionValue="name"
                                 size="6"
                                 class="isRequired"
                                 isDisabled="true"
                                 controller="pcore"
                                 action="educationDegreeAutoComplete"
                                 name="educationDegreesShow"
                                 id="educationDegrees"
                                 label="${message(code: 'jobRequisition.educationDegrees.label', default: 'educationDegrees')}"
                                 values="${jobRequisition?.transientData?.educationDegreeMapList}"
                                 multiple="true"/>
            </div>
            <div class="hiddenEducationDegreesDiv">
                <g:each in="${jobRequisition?.transientData?.educationDegreeMapList}" var="educationDegree"
                        status="index">
                    <input type='hidden' name='educationDegrees' id='educationDegree-${index + 1}'
                           value='${educationDegree[0]}'/>
                </g:each>
            </div>
            <div  class="educationMajorDiv">
                <el:autocomplete optionKey="id"
                                 optionValue="name"
                                 size="6"
                                 class=""
                                 controller="pcore"
                                 action="educationMajorAutoComplete"
                                 name="educationMajors"
                                 id="educationMajors"
                                 label="${message(code: 'jobRequisition.educationMajors.label', default: 'educationMajors')}"
                                 values="${jobRequisition?.transientData?.educationMajorMapList}"
                                 multiple="true"/>
            </div>
        </el:formGroup>

        <el:formGroup>
            <el:autocomplete
                    optionKey="id"
                    optionValue="name"
                    size="6"
                    class=" "
                    controller="pcore"
                    action="governorateAutoComplete"
                    name="governorates"
                    label="${message(code: 'jobRequisition.governorateId.label', default: 'governorateId')}"
                    values="${jobRequisition?.transientData?.governorateMapList}"
                    multiple="true"/>

            <el:autocomplete
                    optionKey="id"
                    optionValue="name"
                    size="6"
                    class=" "
                    controller="pcore"
                    action="governorateAutoComplete"
                    name="fromGovernorates"
                    label="${message(code: 'jobRequisition.fromGovernorates.label', default: 'governorateId')}"
                    values="${jobRequisition?.transientData?.fromGovernorateMapList}"
                    multiple="true"/>

        </el:formGroup>


        <el:formGroup>

            <el:autocomplete optionKey="id"
                             optionValue="name"
                             size="6"
                             class=""
                             paramsGenerateFunction="getMilitaryRankParams"
                             controller="militaryRank"
                             action="autocomplete"
                             name="proposedRank.id"
                             label="${message(code: 'jobRequisition.proposedRank.label', default: 'proposedRank')}"
                             values="${[[jobRequisition?.proposedRank?.id, jobRequisition?.proposedRank?.descriptionInfo?.localName]]}"/>

            <el:autocomplete
                    optionKey="id"
                    optionValue="name"
                    size="6"
                    class=""
                    controller="pcore"
                    action="maritalStatusAutoComplete"
                    name="maritalStatusId"
                    label="${message(code: 'jobRequisition.maritalStatusId.label', default: 'maritalStatusId')}"
                    values="${[[jobRequisition?.maritalStatusId, jobRequisition?.transientData?.maritalStatusName]]}"/>

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
                             values="${jobRequisition?.inspectionCategories.collect {
                                 [it.id, it.descriptionInfo.localName]
                             }}"
                             multiple='true'/>

            <el:select valueMessagePrefix="EnumSexAccepted"
                       from="${ps.gov.epsilon.hr.enums.jobRequisition.v1.EnumSexAccepted.values()}"
                       name="sexTypeAccepted"
                       size="6"
                       class=" "
                       label="${message(code: 'jobRequisition.sexTypeAccepted.label', default: 'sexTypeAccepted')}"
                       value="${jobRequisition?.sexTypeAccepted}"/>
        </el:formGroup>


        <el:formGroup>
            <el:textArea name="jobDescription"
                         size="6"
                         class=""
                         label="${message(code: 'jobRequisition.jobDescription.label', default: 'jobDescription')}"
                         value="${jobRequisition?.jobDescription}"/>
        </el:formGroup>
    </lay:widgetBody>
</lay:widget>
<br/>


<lay:widget transparent="true" color="blue" title="${g.message(code: "jobRequisition.otherRequirements.label")}">
    <lay:widgetBody>
        <br/>

        <div class="form-group ">
            <div class="col-sm-6 pcp-form-control ">
                <label class="col-sm-4 control-label no-padding-right text-left">
                    ${message(code: 'jobRequisition.Age.label', default: 'Age')}
                </label>

                <div class="col-sm-8">
                    <div id="age" class="input-group">
                        <input id="fromAge"
                               value="${jobRequisition?.fromAge}"
                               class="form-control isNumber input-integer null"
                               type="text"
                               name="fromAge">
                        <span class="input-group-addon">
                            <i class="ace-icon icon-sort-numeric"></i>
                        </span>
                        <input id="toAge"
                               value="${jobRequisition?.toAge}"
                               class="form-control isNumber input-integer"
                               type="text"
                               name="toAge">
                    </div>
                </div>
            </div>


            <div class="col-sm-6 pcp-form-control ">
                <label class="col-sm-4 control-label no-padding-right text-left">
                    ${message(code: 'jobRequisition.height.label', default: 'Height')}
                </label>
                <div class="col-sm-8">
                    <div id="tall" class="input-group">
                        <input id="fromHeight"
                               value="${jobRequisition?.fromHeight}"
                               class="form-control isDecimal input-decimal null"
                               type="text"
                               name="fromHeight">
                        <span class="input-group-addon">
                            <i class="ace-icon icon-sort-numeric-outline"></i>
                        </span>
                        <input id="toHeight"
                               value="${jobRequisition?.toHeight}"
                               class="form-control isDecimal input-decimal"
                               type="text"
                               name="toHeight">
                    </div>
                </div>
            </div>
        </div>

        <div class="form-group ">
            <div class="col-sm-6 pcp-form-control ">
                <label class="col-sm-4 control-label no-padding-right text-left">
                    ${message(code: 'jobRequisition.Weight.label', default: 'Weight')}
                </label>

                <div class="col-sm-8">
                    <div id="weight" class="input-group">
                        <input id="fromWeight"
                               value="${jobRequisition?.fromWeight}"
                               class="form-control isDecimal input-decimal null"
                               type="text"
                               name="fromWeight">
                        <span class="input-group-addon">
                            <i class="ace-icon icon-sort-numeric-outline"></i>
                        </span>
                        <input id="toWeight"
                               value="${jobRequisition?.toWeight}"
                               class="form-control isDecimal input-decimal"
                               type="text"
                               name="toWeight">
                    </div>
                </div>
            </div>

            <el:textField name="note"
                          size="6"
                          class=""
                          label="${message(code: 'jobRequisition.note.label', default: 'note')}"
                          value="${jobRequisition?.note}"/>
        </div>
    </lay:widgetBody>
</lay:widget>
<br/>


<div class="col-md-12">
    <table width="100%">
        <tr>
            <td style="width: 100%;">
                <h4 class=" smaller lighter blue">${message(code: 'jobRequisition.previousWork.label')}</h4>
            </td>
            <td width="160px" align="left">
                <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                              link="${createLink(controller: 'jobRequisition', action: 'previousWorkModal', id: dispatchList?.encodedId)}"
                              label="${message(code: 'jobRequisition.previousWork.add.btn')}">
                    <i class="ace-icon fa"></i>
                </el:modalLink>
            </td>
        </tr>
    </table>
    <hr style="margin-top: 0px;margin-bottom: 9px;"/>
</div>

<el:formGroup>
    <div class="col-md-12" id="tableDiv">
        <lay:table styleNumber="1" id="previousWorkTable">
            <lay:tableHead title="${message(code: 'jobRequisition.workExperience.periodInYears.label')}"/>
            <lay:tableHead title="${message(code: 'jobRequisition.workExperience.professionType.label')}"/>
            <lay:tableHead title="${message(code: 'jobRequisition.workExperience.competency.label')}"/>
            <lay:tableHead title="${message(code: 'jobRequisition.workExperience.otherSpecifications.label')}"/>
            <lay:tableHead title="${message(code: 'default.actions.label')}"/>
            <g:each in="${jobRequisition?.requisitionWorkExperiences?.sort { it?.id }}" var="workExperience"
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


            <g:if test="${!jobRequisition?.requisitionWorkExperiences}">
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