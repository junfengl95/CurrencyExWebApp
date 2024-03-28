import { useState, useEffect } from 'react'
import axios from 'axios';

const useAxios = (url) => {
    const [data, setData] = useState([]);
    const [error, setError] = useState(null);
    const [loaded, setLoaded] = useState(false);

    useEffect(()=>{
        const fetchData = async () =>{
            try {
                setLoaded(true)
                const response = await axios(url);
                setData(response.data)
            } catch(error){
                setError(error)
            } finally{
                setLoaded(false)
            }
        }
        fetchData();
    }, [url])
    return [data, error, loaded]
}

export default useAxios;