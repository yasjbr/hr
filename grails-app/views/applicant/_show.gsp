<lay:showWidget size="6" title="${g.message(code: "applicant.basic.info.label")}">
    <lay:showElement value="${applicant?.transientData?.personDTO?.recentCardNo}" type="String"
                     label="${message(code: 'applicant.recentCardNo.label', default: 'recentCardNo')}"/>

    <lay:showElement value="${applicant?.transientData?.personDTO?.dateOfBirth}" type="ZonedDate"
                     label="${message(code: 'applicant.dateOfBirth.label', default: 'dateOfBirth')}"/>

    <lay:showElement value="${applicant?.transientData?.birthPlace}" type="String"
                     label="${message(code: 'applicant.birthPlace.label', default: 'birthPlace')}"/>
    <lay:showElement value="${applicant?.transientData?.personDTO?.genderType?.descriptionInfo?.localName}"
                     type="String"
                     label="${message(code: 'applicant.gender.label', default: 'gender')}"/>

    <lay:showElement value="${applicant?.transientData?.personMaritalStatus?.maritalStatus?.descriptionInfo?.localName}" type="String"
                     label="${message(code: 'applicant.maritalStatus.label', default: 'maritalStatus')}"/>

    <lay:showElement value="${applicant?.age?.longValue()}" type="Integer"
                     label="${message(code: 'applicant.age.label', default: 'age')}"/>

    <lay:showElement value="${applicant?.weight}" type="Double"
                     label="${message(code: 'applicant.weight.label', default: 'weight')}"/>

    <lay:showElement value="${applicant?.height}" type="Double"
                     label="${message(code: 'applicant.height.label', default: 'height')}"/>

    <lay:showElement value="${applicant?.specialMarksNote}" type="String"
                     label="${message(code: 'applicant.specialMarksNote.label', default: 'specialMarksNote')}"/>


    <lay:showElement value="${applicant?.transientData?.location}" type="String"
                     label="${message(code: 'applicant.location.label', default: 'Location')}"/>

</lay:showWidget>



<lay:showWidget size="6" title="${g.message(code: "applicant.mother.info.label")}">
    <lay:showElement value="${applicant?.motherName}" type="String"
                     label="${message(code: 'applicant.motherName.label', default: 'motherName')}"/>
    <lay:showElement value="${applicant?.transientData?.motherProfessionName}" type="String"
                     label="${message(code: 'applicant.motherProfessionType.label', default: 'motherProfessionType')}"/>
    <lay:showElement value="${applicant?.motherJobDesc}" type="String"
                     label="${message(code: 'applicant.motherJobDesc.label', default: 'motherJobDesc')}"/>
</lay:showWidget>

%{--the bellow rows to add space btw show widget--}%
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>

<lay:showWidget size="6" title="${g.message(code: "applicant.father.info.label")}">
    <lay:showElement value="${applicant?.fatherName}" type="String"
                     label="${message(code: 'applicant.fatherName.label', default: 'fatherName')}"/>
    <lay:showElement value="${applicant?.transientData?.fatherProfessionName}" type="String"
                     label="${message(code: 'applicant.fatherProfessionType.label', default: 'fatherProfessionType')}"/>
    <lay:showElement value="${applicant?.fatherJobDesc}" type="String"
                     label="${message(code: 'applicant.fatherJobDesc.label', default: 'fatherJobDesc')}"/>
</lay:showWidget>


<el:row/>
<br/>

<lay:showWidget size="6" title="${g.message(code: "applicant.relative.info.label")}">

    <lay:showElement value="${applicant?.relativesInMilitaryFirms}" type="String"
                     label="${message(code: 'applicant.relativesInMilitaryFirms.label', default: 'relativesInMilitaryFirms')}"/>

    <lay:showElement value="${applicant?.relativesInCivilianFirm}" type="String"
                     label="${message(code: 'applicant.relativesInCivilianFirm.label', default: 'relativesInCivilianFirm')}"/>
</lay:showWidget>
<lay:showWidget size="6" title="${g.message(code: "applicant.other.info.label")}">

    <lay:showElement value="${applicant?.recruitmentCycle?.name}" type="String"
                     label="${message(code: 'applicant.recruitmentCycle.label', default: 'recruitmentCycle')}"/>

    <lay:showElement value="${applicant?.vacancy?.job?.descriptionInfo?.localName}" type="String"
                     label="${message(code: 'applicant.vacancy.label', default: 'vacancy')}"/>
    <lay:showElement value="${applicant?.nominationParty}" type="String"
                     label="${message(code: 'applicant.nominationParty.label', default: 'fatherName')}"/>

    <lay:showElement value="${applicant?.archiveNumber}" type="String"
                     label="${message(code: 'applicant.archiveNumber.label', default: 'archiveNumber')}"/>

</lay:showWidget>



<el:row/>
<el:row/>
<el:row/>

<g:if test="${!isRecruitmentCycleTab}">
<div class="clearfix form-actions text-center">
    <g:set var='myArray' value='[encodedId: "${applicant?.encodedId}"]'/>
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'applicant', action: 'edit', params: myArray)}'"/>

    <g:if test="${applicant?.traineeListEmployee?.traineeList?.id}">

        <btn:button messageCode="traineeList.label" color="pink"
                    icon="fa fa-bars" size="bigger" class="width-135"

                    onClick="window.location.href='${createLink(controller: 'traineeList', action: 'manageTraineeList', params: [encodedId:applicant?.traineeListEmployee?.traineeList?.encodedId])}'"/>
    </g:if>

    <g:if test="${applicant?.recruitmentListEmployee?.recruitmentList?.id}">

        <btn:button messageCode="recruitmentList.label" color="pink"
                    icon="fa fa-bars" size="bigger" class="width-135"
                    onClick="window.location.href='${createLink(controller: 'recruitmentList', action: 'manageRecruitmentList', params: [encodedId:applicant?.recruitmentListEmployee?.recruitmentList?.encodedId])}'"/>
    </g:if>

    <btn:backButton goToPreviousLink="true" />
</div>
</g:if>