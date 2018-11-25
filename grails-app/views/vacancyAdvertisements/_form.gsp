<el:formGroup>
    <el:textField name="title" size="8" class=" isRequired"
                  label="${message(code: 'vacancyAdvertisements.title.label', default: 'title')}"
                  value="${vacancyAdvertisements?.title}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="postingDate" size="8" class=" isRequired" setMinDateFor="closingDate"
                  label="${message(code: 'vacancyAdvertisements.postingDate.label', default: 'postingDate')}"
                  value="${vacancyAdvertisements?.postingDate}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="recruitmentCycle"
                     action="autocomplete" name="recruitmentCycle.id"
                     paramsGenerateFunction="recruitmentCycleParams" id="recruitmentCycle"
                     label="${message(code: 'vacancyAdvertisements.recruitmentCycle.label', default: 'recruitmentCycle')}"
                     values="${[[vacancyAdvertisements?.recruitmentCycle?.id, vacancyAdvertisements?.recruitmentCycle?.name]]}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="closingDate" size="8" class=" isRequired"
                  label="${message(code: 'vacancyAdvertisements.closingDate.label', default: 'closingDate')}"
                  value="${vacancyAdvertisements?.closingDate}"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="toBePostedOn" size="8" class=""
                  label="${message(code: 'vacancyAdvertisements.toBePostedOn.label', default: 'toBePostedOn')}"
                  value="${vacancyAdvertisements?.toBePostedOn}"/>
</el:formGroup>
<el:formGroup>
    <el:textArea name="description" size="8" class=""
                 label="${message(code: 'vacancyAdvertisements.description.label', default: 'description')}"
                 value="${vacancyAdvertisements?.description}"/>
</el:formGroup>

<div>
    <div class="col-md-12">
        <table width="100%">
            <tr>
                <td style="width: 100%;">
                    <h4 class=" smaller lighter blue">${g.message(code: "vacancyAdvertisements.vacancies.label")}</h4>
                </td>
                <td width="160px" align="left">
                    %{--add edit button to select new  vacancy --}%
                    <el:modalLink
                            link="${createLink(controller: 'vacancyAdvertisements', action: 'getVacancies')}"
                            preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                            label="${message(code: 'vacancyAdvertisements.addVacancy.label')}">
                    </el:modalLink>
                </td>
            </tr>
        </table>
        <hr style="margin-top: 0px;margin-bottom: 9px;"/>
    </div>
    <el:formGroup id="vacancyDiv">
        <div class="col-md-12">
        %{--represent vacancy in formal way--}%
            <lay:table styleNumber="1" id="vacancyTable1">
                <lay:tableHead title="${message(code: 'vacancy.recruitmentCycle.label')}"/>
                <lay:tableHead title="${message(code: 'vacancy.job.descriptionInfo.localName.label')}"/>
                <lay:tableHead title="${message(code: 'vacancy.numberOfPositions.label')}"/>
                <lay:tableHead title="${message(code: 'vacancy.vacancyStatus.label')}"/>
                <lay:tableHead title="${message(code: 'interview.delete.label')}"/>

                <g:if test="${!vacancyAdvertisements?.joinedVacancyAdvertisement}">
                    <rowElement>
                        <tr class='center vacancies-rows' id="row-0">
                            <td class='center'></td>
                            <td class='center'></td>
                            <td class='center'></td>
                            <td class='center'></td>
                            <td class='center'></td>
                        </tr>
                    </rowElement>
                </g:if>
                <g:else>
                    <g:each in="${vacancyAdvertisements?.joinedVacancyAdvertisement?.sort { it?.id }}" var="joinedVacancyAdv"
                            status="index">

                        <rowElement>
                            <tr class='center vacancies-rows' id="row-${index+1}">
                                <td class='center'>${joinedVacancyAdv?.vacancy?.recruitmentCycle}
                                    <input type='hidden' name='vacancy' value='${joinedVacancyAdv?.vacancy?.id}'></td>
                                <td class='center'>${joinedVacancyAdv?.vacancy?.job?.descriptionInfo?.localName}</td>
                                <td class='center'>${joinedVacancyAdv?.vacancy?.numberOfPositions}</td>
                                <td class='center'>${message(code: 'EnumVacancyStatus.' + joinedVacancyAdv?.vacancy?.vacancyStatus)}</td>
                                <td class='center'>
                                    <span class='delete-action'>
                                        <a style='cursor: pointer;'
                                           class='red icon-trash '
                                           onclick='deleteRow(${index+1})'
                                           title='<g:message code='default.button.delete.label'/>'>
                                        </a>
                                    </span>
                                </td>
                            </tr>
                        </rowElement>

                    </g:each>
                </g:else>



            </lay:table>
        </div>
    </el:formGroup>
</div>


