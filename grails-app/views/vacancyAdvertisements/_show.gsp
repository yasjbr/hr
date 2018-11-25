<el:row/>
<br/>

<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${vacancyAdvertisements?.title}" type="String"
                     label="${message(code: 'vacancyAdvertisements.title.label', default: 'title')}"/>
    <lay:showElement value="${vacancyAdvertisements?.recruitmentCycle?.name}" type="String"
                     label="${message(code: 'vacancyAdvertisements.recruitmentCycle.label', default: 'recruitmentCycle')}"/>
    <lay:showElement value="${vacancyAdvertisements?.postingDate}" type="ZonedDate"
                     label="${message(code: 'vacancyAdvertisements.postingDate.label', default: 'postingDate')}"/>
    <lay:showElement value="${vacancyAdvertisements?.closingDate}" type="ZonedDate"
                     label="${message(code: 'vacancyAdvertisements.closingDate.label', default: 'closingDate')}"/>
    <lay:showElement value="${vacancyAdvertisements?.description}" type="String"
                     label="${message(code: 'vacancyAdvertisements.description.label', default: 'description')}"/>
    <lay:showElement value="${vacancyAdvertisements?.toBePostedOn}" type="String"
                     label="${message(code: 'vacancyAdvertisements.toBePostedOn.label', default: 'toBePostedOn')}"/>
</lay:showWidget>

<div class="row">&emsp;</div>

<lay:showWidget size="12"
                title="${message(code: 'vacancyAdvertisements.vacancies.label', default: 'vacancies List')}">

    <table width="98%" frame="border" id="detailsTable"
           style="border-color: #336199; display:block; width:100%; ">
        <thead style=" width:100%; display: table;">
        <tr style="border-bottom:1pt dotted #dcebf7; ">
            <td width="10%"
                style="color: #336199;background-color: #edf3f4; padding-right: 6px;height: 30px;">${message(code: 'vacancy.recruitmentCycle.label')}</td>
            <td width="30%"
                style="color: #336199;background-color: #edf3f4; padding-right: 6px;">${message(code: 'vacancy.job.descriptionInfo.localName.label')}</td>
            <td width="30%"
                style="color: #336199;background-color: #edf3f4; padding-right: 6px;">${message(code: 'vacancy.numberOfPositions.label')}</td>
            <td width="30%"
                style="color: #336199;background-color: #edf3f4; padding-right: 6px;">${message(code: 'vacancy.vacancyStatus.label')}</td>

        </tr>
        </thead>
        <tbody class="workExperiences" style="overflow: auto;display: -moz-stack;height: 100px;width: 100%;">

        <g:each in="${vacancyAdvertisements?.joinedVacancyAdvertisement?.sort { it?.id }}" var="joinedVacancy"
                status="index">
            <tr width='100%' id="row-${index + 1}" style="width: 100% ;border-bottom:1pt dotted #dcebf7;">

                <td width="10%" style="padding-right: 6px;">
                    ${joinedVacancy?.vacancy?.recruitmentCycle}
                </td>
                <td width="30%" style="padding-right: 6px;">
                    ${joinedVacancy?.vacancy?.job?.descriptionInfo?.localName}
                </td>
                <td width="30%" style="padding-right: 6px;">
                    ${joinedVacancy?.vacancy?.numberOfPositions}
                </td>
                <td width="30%" style="padding-right: 6px;">
                    ${message(code: 'EnumVacancyStatus.' + joinedVacancy?.vacancy?.vacancyStatus, default: joinedVacancy?.vacancy?.vacancyStatus)}
                </td>

            </tr>
        </g:each>
        </tbody>
    </table>
</lay:showWidget>

<div class="row">&emsp;
</div>