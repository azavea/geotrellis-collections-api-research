task default: %[build]

desc "Build app & api"
task :build do
    puts "Building app container ->"
    sh "docker-compose -f docker-compose.yml run --rm --no-deps app yarn"
end

desc "Log in to container shell"
task :console do
    if ARGV.count === 2 && ['app', 'api'].include?(ARGV[1])
        if ARGV[1] === 'app'
            puts "Logging into app container shell ->"
            sh "docker-compose exec app /bin/bash"
        else
            puts "Logging into api console ->"
            Dir.chdir('./api') do
                sh "./sbt console"
            end
        end
    else
        puts "Usage ->"
        puts "rake console app -> login to app container shell"
        puts "rake console api -> login to API container shell"
    end
end

desc "./sbt ~reStart"
task :sbt do
    puts "Starting api service ->"
    Dir.chdir('./api') do
        sh "./sbt ~reStart"
    end
end

desc "Run servers"
task :server do
    trap('SIGINT') { sh "docker-compose down"; exit }
    puts "Starting app server ->"
    sh "docker-compose up -d app"
    Rake::Task["sbt"].execute
end

desc "Configure Tmuxinator"
task :tmux do
    puts "Configuring Tmuxinator ->"
    tmuxinator_dir = File.expand_path(Dir.home + "/.tmuxinator")
    if File.exist?(tmuxinator_dir)
        sh "ln -s #{Dir.pwd}/tmux.yml #{tmuxinator_dir}/geotrellis-research.yml"
    else
        puts "Tmuxinator not installed"
    end
end
