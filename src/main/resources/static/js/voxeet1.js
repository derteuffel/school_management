const removeVideoNode = (participant) => {
    let videoNode = document.getElementById('video-' + participant.id);

    if (videoNode) {
        videoNode.parentNode.removeChild(videoNode);
    }
}
const constraints = {
    audio: true,
    video: true
}

    VoxeetSDK.initialize('N2wzZXJrdG1zcTc3cQ==', 'NzRqZ2pocGNmdmNxa2Q5YjZob2FoYWQ0MzU=')
VoxeetSDK.conference.on('streamAdded', (participant, stream) => {
    addVideoNode(participant, stream);
})
    VoxeetSDK.conference.on('streamRemoved', (participant) => {
        removeVideoNode(participant);
    })
const joinButton = document.getElementById('join')
const join = async ()=>{
    $('#body').preloader()
    await VoxeetSDK.session.open({name:document.getElementById('username').value})
    const conference = await VoxeetSDK.conference.fetch(document.getElementById('conferenceId').value)
    console.log(conference)
    if(!conference){

            $('#body').preloader('remove')
            $('#joinError').css('display','block')
    }
    else
    {
    VoxeetSDK.conference.join(conference,{constraints}).then(()=>{
            $('#body').preloader('remove')
            document.getElementById('video-super-container').style.display='flex'

    }


    )
        .catch(e=>{
            $('#body').preloader('remove')
            $('#joinError').css('display','block')
            VoxeetSDK.conference.stopVideo(VoxeetSDK.session.participant)
            console.log(e)
        })
    }
}
joinButton.addEventListener('click',join)