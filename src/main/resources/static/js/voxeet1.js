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
const joinButtonAudio = document.getElementById('joinAudio')
const join = async ()=>{
    $('#body').preloader()
    await VoxeetSDK.session.open({name:document.getElementById('username').value})
    const conference = await VoxeetSDK.conference.fetch(document.getElementById('conferenceId').value)
    console.log(conference)
    if(!conference){

            $('#body').preloader('remove')
            $('#joinError').css('display','block')
        VoxeetSDK.conference.stopVideo(VoxeetSDK.session.participant)
    }
    else
    {
    VoxeetSDK.conference.join(conference,{constraints}).then(()=>{
            $('#body').preloader('remove')
            document.getElementById('video-super-container').style.display='flex'

    }  ).catch(e=>{
            $('#body').preloader('remove')
            $('#joinError').css('display','block')
            VoxeetSDK.conference.stopVideo(VoxeetSDK.session.participant)
            VoxeetSDK.conference.leave()
            console.log('error',e)
        })
    }
}
const interval = ()=>{
    const date = new Date()
    setInterval(()=>{
        const distance= new Date().getTime()- date.getTime()
        var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        var seconds = Math.floor((distance % (1000 * 60)) / 1000)

        minutes = minutes < 10 ? "0" + minutes : minutes
        seconds = seconds < 10 ? "0" + seconds : seconds
        $('#countDown').text(`${minutes}:${seconds}`)
    },1000)
}
const joinAudio = async ()=>{
    $('#body').preloader()
    await VoxeetSDK.session.open({name:document.getElementById('username').value})
    const conference = await VoxeetSDK.conference.fetch(document.getElementById('conferenceId').value)
    if(!conference){

        $('#joinError').css('display','block')
        $('#body').preloader('remove')
    }
    else
    {
        VoxeetSDK.conference.join(conference,{audio:true,video:false}).then(()=>{
            interval()
            $('#body').preloader('remove')
            document.getElementById('audioComponent').style.display='flex'
        }  ).catch(e=>{
            $('#joinError').css('display','block')
            $('#body').preloader('remove')
            console.log('error',e)
        })
    }
}
joinButton.addEventListener('click',join)
joinButtonAudio.addEventListener('click',joinAudio)