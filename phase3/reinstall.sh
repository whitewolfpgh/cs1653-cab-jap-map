rm -rf *.class
rm -rf *.bin
rm -rf shared_files
rm -rf certs
rm -rf client_certs
rm -rf known_hosts
mkdir certs
mkdir client_certs
mkdir shared_files
mkdir known_hosts
echo "application fully reinstalled. compiling....."
./build.sh
echo "compile complete.  run the following on "
echo "java RunGroupServer PORT_NUMBER"
echo "java RunFileServer PORT_NUMBER"
echo -e "java TextUI\n"
echo "for in-client help, issue the 'help' command at the TextUI prompt"
