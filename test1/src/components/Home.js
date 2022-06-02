import React, { useState } from 'react'
import '../App.css';
import { useSelector, useDispatch } from 'react-redux';
import Header from '../Fixed/Header'
import Footer from '../Fixed/Footer';
import { useNavigate, useParams } from "react-router-dom";
import { deleteWord, completedWord } from '../redux/modules/word'
import styled from "styled-components";


const Home = (props) => {
  const dispatch = useDispatch();
  let a =  useSelector((state) => state.id )
  console.log(a)
  const dic_list = useSelector((state) => state.word.list);
  console.log(dic_list)
      const completedWordList = () => {
            dispatch(completedWord(dic_list))
          }
      const deleteWordList = () => {
            dispatch(deleteWord(dic_list))
          }
          
  return (
    <>
      <Header />
      <div className='empty'></div>
      <div className='wrap'>
        {dic_list.map((dic, idx) => {
          return (
            <ItemStyle key={idx} completed={dic.completed} id={idx}>
              <div className='del'>
                <span onClick={completedWordList}>⭕</span>
                <span>/</span>
                <span onClick={deleteWordList}>🚫</span>
              </div>
              <div style={{ fontSize: "23px" }}>{dic.word}</div>
              <div>[{dic.express}]</div>
              <div>{dic.meaning}</div>
              <div style={{ color: "blue" }}>{dic.example}</div>
              <div style={{ color: "blue" }}>{dic.explain}</div>
            </ItemStyle>
          )
        })}
      </div>
      <Footer />
    </>
  )
}

const ItemStyle = styled.div`
  width: 300px;
  height: 140px;
  border: rgb(209, 207, 207) 1px solid;
  padding: 10px 20px;
  /* display: flex;
  flex-direction: column;
  justify-content: center; */
  border-radius: 30px;
  margin: 10px;
  background-color: ${(props) => props.completed ? "tomato" : "rgb(168, 206, 221)"};
`

export default Home