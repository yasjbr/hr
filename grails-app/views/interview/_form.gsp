<div style="padding-right: 40px;,padding-bottom: 15px;">
    <h4 class=" smaller lighter blue">
        ${message(code: 'interview.info.label')}</h4> <hr/></div>

<el:formGroup>
    <el:textField name="description" size="8" class=" isRequired"
                  label="${message(code: 'interview.description.label', default: 'description')}"
                  value="${interview?.description}"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="fromDate" size="8" class=" isRequired" setMinDateFor="toDate"
                  label="${message(code: 'interview.fromDate.label', default: 'fromDate')}"
                  value="${interview?.fromDate}"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="toDate" size="8" class=" isRequired"
                  label="${message(code: 'interview.toDate.label', default: 'toDate')}" value="${interview?.toDate}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="recruitmentCycle"
                     action="autocomplete" name="recruitmentCycle.id"
                     paramsGenerateFunction="interviewParams"
                     label="${message(code: 'interview.recruitmentCycle.label', default: 'recruitmentCycle')}"
                     values="${[[interview?.recruitmentCycle?.id, interview?.recruitmentCycle?.name]]}"/>
</el:formGroup>

<el:formGroup>
    <el:textArea name="note" size="8" class="" label="${message(code: 'interview.note.label', default: 'note')}"
                 value="${interview?.note}"/>
</el:formGroup>


<br/>






<br/>


<div style="padding-right: 40px;,padding-bottom: 15px;">
    <h4 class=" smaller lighter blue">
        ${message(code: 'interview.location.label')}</h4> <hr/></div>

<el:hiddenField name="edit_locationId" value="${interview?.locationId}"/>
<br/>
<g:render template="/pcore/location/staticWrapper"
          model="[location          : interview?.transientData?.locationDTO,
                  isRequired        : true,
                  size              : 8,
                  isRegionRequired  : false,
                  isCountryRequired : false,
                  isDistrictRequired: false]"/>
<el:formGroup>
    <el:textArea name="unstructuredLocation" size="8" class=""
                 label="${message(code: 'interview.unstructuredLocation.label', default: 'unstructuredLocation')}"
                 value="${interview?.unstructuredLocation}"/>
</el:formGroup>

%{---------------------------------------------------------------------------------------}%


<div>
    <div class="col-md-12">
        <table width="100%">
            <tr>
                <td style="width: 100%;">
                    <h4 class=" smaller lighter blue">${g.message(code: "applicant.vacancy.information.label")}</h4>
                </td>
                <td width="160px" align="left">
                    %{--add edit button to select new  vacancy --}%
                    <el:modalLink
                            link="${createLink(controller: 'applicant', action: 'getVacancies')}"
                            preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                            label="${message(code: 'applicant.select.vacancy.label')}">
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
        <rowElement>
            <g:if test="${interview?.vacancy}">
                <tr class='center' id='row-0'>
                    <td class='center'>
                        <el:hiddenField name="vacancy.id" value="${interview?.vacancy?.id}"/>
                        ${interview?.vacancy?.recruitmentCycle}</td>
                    <td class='center'>${interview?.vacancy?.job?.descriptionInfo?.localName}</td>
                    <td class='center'>${interview?.vacancy?.numberOfPositions}</td>
                    <td class='center'>${message(code: 'EnumVacancyStatus.' + interview?.vacancy?.vacancyStatus)}</td>
                </tr>
            </g:if>
            <g:else>
                <tr id="row-0" class='center'>
                    <td class='center'></td>
                    <td class='center'></td>
                    <td class='center'></td>
                    <td class='center'></td>
                </tr>
            </g:else>
        </rowElement>
    </lay:table>
        </div>
</el:formGroup>
</div>

<br/>

%{--------------------------------------------------------------------------------------}%


<div>

    <div class="col-md-12">

        <table width="100%">
            <tr>
                <td style="width: 80%;">
                    <h4 class=" smaller lighter blue">${message(code: 'interview.committeeRoles.label')}</h4>
                </td>
                <td width="180px" align="left">
                    <button type="button" class="btn  btn-bigger  btn-sm  btn-primary  btn-round"
                            id="addCommitteeRoleBtn"
                            onclick='openCommitteeRoleModal()'>
                        <g:message code="interview.committeeRole.add.btn"/>
                    </button>
                </td>
            </tr>
        </table>
        <hr style="margin-top: 0px;margin-bottom: 8px;"/>
    </div>

    <el:formGroup>
        <div class="col-md-12" id="tableDiv">
            <lay:table styleNumber="1" id="detailsTable">
                <lay:tableHead title="${message(code: 'interview.committeeRole.label')}"/>
                <lay:tableHead title="${message(code: 'interview.committeeName.label')}"/>
                <lay:tableHead title="${message(code: 'interview.delete.label')}"/>

                <g:each in="${interview?.committeeRoles?.sort { it?.id }}" var="committeeRole"
                        status="index">
                    <rowElement>
                    <tr id="roles-row-${index + 1}" class='center'>

                        <td class='center'>
                            ${committeeRole?.committeeRole?.descriptionInfo?.localName}
                        </td>
                        <td class='center'>
                            ${committeeRole?.partyName}
                        </td>

                        <td class='center'>
                            <input type='hidden' name='committeeRole' value='${committeeRole?.committeeRole?.id}'>
                            <input type='hidden' name='partyName' value='${committeeRole?.partyName}'/>

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


                <g:if test="${!interview?.committeeRoles}">
                    <rowElement>
                        <tr id="roles-row-0" class='center'>
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
<el:modal preventCloseOutSide="true" name="committeeRoleModal" id="committeeRoleModal"
          width="50%" hideCancel="true" withAttachment="true" method="post"
          title="${g.message(code: "interview.committeeRole.label")}">

    <el:modalButton class="btn  btn-bigger  btn-sm  btn-primary  btn-round"
                    icon="ace-icon fa fa-floppy-o"
                    onClick="addCommitteeRole()"
                    id="committeeRoleAddBtn"
                    message="${g.message(code: "interview.committeeRole.add.label")}"/>

    <el:modalButton calss="btn  btn-bigger  btn-sm  btn-light  btn-round"
                    id="committeeRoleCancelBtn"
                    icon="ace-icon fa icon-cancel"
                    onClick="closeCommitteeRoleModal()"
                    message="${g.message(code: "interview.committeeRole.modal.close.label")}"/>

    <msg:modal/>

    <el:formGroup>
        <el:autocomplete label="${message(code: 'interview.committeeRole.label', default: 'committee role')}"
                         name="committeeRoleSelected"
                         class=" isRequired"
                         controller="committeeRole" action="autocomplete" size="12" id="committeeRoleId"/>

    </el:formGroup>

    <el:formGroup>
        <el:textField name="committeeName" id="committeeNameId" size="12"
                      class=" isRequired"
                      label="${message(code: 'interview.committeeName.label', 'committee name')}"/>
    </el:formGroup>

</el:modal>
