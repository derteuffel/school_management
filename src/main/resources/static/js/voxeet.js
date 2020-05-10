VoxeetSDK.initialize('N2wzZXJrdG1zcTc3cQ==', 'NzRqZ2pocGNmdmNxa2Q5YjZob2FoYWQ0MzU=')
VoxeetSDK.conference.on('streamAdded', (participant, stream) => {
    addVideoNode(participant, stream);
})
VoxeetSDK.conference.on('streamRemoved', (participant) => {
    removeVideoNode(participant);
})
const removeVideoNode = (participant) => {
    let videoNode = document.getElementById('video-' + participant.id);

    if (videoNode) {
        videoNode.parentNode.removeChild(videoNode);
    }
}
const main = async (name,to) => {
    try {
        await VoxeetSDK.session.open({name})
        VoxeetSDK.conference.create({ alias: name })
            .then((conference) =>{
                    fetch(`https://ecoles.yesbanana.org/sendMail/${to}/${conference.id}`).then(()=>{
                        console.log('great')
                    })
                    return VoxeetSDK.conference.join(conference, {})
            }
            )
            .then(() => {

                VoxeetSDK.conference.startVideo(VoxeetSDK.session.participant).then(
                    ()=>{
                        $('#body').preloader('remove')
                        document.getElementById('video-super-container').style.display='flex'
                          }
                ).catch(e=>{console.log(e);$('#body').preloader('remove')})



            })
            .catch((e) => {console.log(e);$('#body').preloader('remove')})

    }


    catch(e){
        console.log(e)
    }
}
const callsButton = document.getElementsByClassName('call')
const callInput = document.getElementsByClassName('callInput')
const crewCall = document.getElementsByClassName('crewCall')
const removeCall = document.getElementsByClassName('removeCall')
const current=0
const call = (e)=>{
    $('#body').preloader()
    main(document.getElementById('directorFirstName').value,e.currentTarget.param)
}
/* crew call */
let users= []
let removeToTheCallList
 removeToTheCallList = (e)=>{
    if(users.indexOf(e.currentTarget.param)!==-1) {
        users.splice(users.indexOf(e.currentTarget.param), 1)
        console.log(users)
        crewCall[e.currentTarget.indice].style.display="inline"
        removeCall[e.currentTarget.indice].style.display="none"
         }
}
let addToTheCallList
 addToTheCallList = (e)=>{
    if(users.indexOf(e.currentTarget.param)==-1)
    {
        users.push(e.currentTarget.param)
        crewCall[e.currentTarget.indice].style.display="none"
        removeCall[e.currentTarget.indice].style.display="inline"
    console.log(users)
    }

}
const crewCallButton = document.getElementById("crewCallButton")
crewCallButton.addEventListener('click',async ()=>{
    const name = document.getElementById('directorFirstName').value
    $('#body').preloader()
    try {
        await VoxeetSDK.session.open({name})
        VoxeetSDK.conference.create({ alias: name })
            .then((conference) =>{
                  users.map(async v=> await fetch(`https://ecoles.yesbanana.org/sendMail/${v}/${conference.id}`)
                  )
                return VoxeetSDK.conference.join(conference, {})
                    }).then(() => {

                VoxeetSDK.conference.startVideo(VoxeetSDK.session.participant).then(
                    ()=>{
                        $('#body').preloader('remove')
                        document.getElementById('video-super-container').style.display='flex'
                    }
                ).catch(e=>{console.log(e);$('#body').preloader('remove')})



            })
            .catch((e) => {console.log(e);$('#body').preloader('remove')})

    }


    catch(e){
        console.log(e)
    }

})
/* crew call */
for(i=0;i<=callInput.length-1;i++){
    callsButton[i].param = callInput[i].value
    callsButton[i].addEventListener('click',call)
    /* crew call */
    crewCall[i].param = callInput[i].value
    crewCall[i].indice = i
    crewCall[i].addEventListener('click',addToTheCallList)
    removeCall[i].param = callInput[i].value
    removeCall[i].indice = i
    removeCall[i].addEventListener('click',removeToTheCallList)
    removeCall[i].style.display="none"
    /* crew call */

}



